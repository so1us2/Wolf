package wolf.model.stage;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.joda.time.DateTime;

import wolf.WolfException;
import wolf.action.Action;
import wolf.action.CommandsAction;
import wolf.action.game.ListPlayersAction;
import wolf.action.game.VoteAction;
import wolf.action.game.VoteCountAction;
import wolf.action.moderator.AbortGameAction;
import wolf.action.moderator.AnnounceAction;
import wolf.action.moderator.GetVotersAction;
import wolf.action.moderator.ReminderAction;
import wolf.action.privatechats.AuthorizePlayerAction;
import wolf.action.privatechats.ChatAction;
import wolf.action.privatechats.JoinRoomAction;
import wolf.action.privatechats.LeaveRoomAction;
import wolf.action.privatechats.ListRoomsAction;
import wolf.action.privatechats.NewRoomAction;
import wolf.action.privatechats.RevokeAuthorizationAction;
import wolf.bot.IBot;
import wolf.model.Faction;
import wolf.model.GameConfig;
import wolf.model.GameSummary;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.VotingHistory;
import wolf.model.chat.ChatServer;
import wolf.model.role.Demon;
import wolf.model.role.Priest;
import wolf.model.role.Vigilante;
import wolf.model.role.Wolf;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

import static com.google.common.collect.Iterables.filter;

public class GameStage extends Stage {

  public static final String NONE_DEAD_MSG =
      "The sun dawns and the village finds that no one has died in the night.";

  private final UUID id = UUID.randomUUID();

  private final CommandsAction commandsAction = new CommandsAction(this);

  private final List<Action> daytimeActions = Lists.newArrayList();

  private final List<Action> adminActions = Lists.newArrayList();

  private final List<Action> chatActions = Lists.newArrayList();

  private final VotingHistory votingHistory = new VotingHistory();

  private final Map<Player, Player> votesToDayKill = Maps.newLinkedHashMap();

  private ChatServer server;

  /**
   * The set of all players (even dead ones).
   */
  private final Set<Player> players;

  private boolean daytime = true;

  private final GameConfig config;

  /**
   * This is stored as part of the GameHistory.
   */
  private final DateTime startDate = new DateTime();


  public GameStage(IBot bot, GameConfig config, Set<Player> players) {
    super(bot);

    this.config = config;
    this.players = ImmutableSortedSet.copyOf(players);

    server = new ChatServer(bot);

    daytimeActions.add(commandsAction);
    daytimeActions.add(new VoteAction(this));
    daytimeActions.add(new VoteCountAction(this));
    daytimeActions.add(new ListPlayersAction(this));

    adminActions.add(new AnnounceAction(this));
    adminActions.add(new AbortGameAction(this));
    adminActions.add(new GetVotersAction(this));
    adminActions.add(new ReminderAction(this));

    chatActions.add(new AuthorizePlayerAction(server));
    chatActions.add(new ChatAction(server));
    chatActions.add(new JoinRoomAction(server));
    chatActions.add(new LeaveRoomAction(server));
    chatActions.add(new ListRoomsAction(server));
    chatActions.add(new NewRoomAction(server));
    chatActions.add(new RevokeAuthorizationAction(server));

    for (Player player : players) {
      player.getRole().setStage(this);
    }

    beginGame();
  }

  private void beginGame() {
    for (Player player : getPlayers()) {
      player.getRole().onGameStart();
    }

    unmutePlayers();

    getBot().sendMessage("Day 1 dawns on the village.");
  }

  private void unmutePlayers() {
    for (Player player : getPlayers()) {
      getBot().unmute(player.getName());
    }
  }

  @Override
  public void handle(IBot bot, String sender, String command, List<String> args) {
    super.handle(bot, sender, command, args);

    if (isNight()) {
      checkForEndOfNight();
    }
  }

  private void checkForEndOfNight() {
    for (Player player : getPlayers()) {
      if (!player.getRole().isFinishedWithNightAction()) {
        return;
      }
    }

    // bring night to a close.
    moveToDay();
  }

  private void moveToDay() {
    // Get anyone who needs to die into the killMap. Upon being added to dying,
    // they must later die so any protection needs to be triggered beforehand.
    Multimap<Player, Player> killMap = TreeMultimap.create();

    if (!getPlayers(Role.WOLF).isEmpty()) {
      List<Player> targets = Lists.newArrayList();
      for (Player p : getPlayers(Role.WOLF)) {
        Wolf wolf = (Wolf) p.getRole();
        targets.add(wolf.getTarget());
      }

      if (targets.contains(null)) {
        // wolves haven't finished choosing yet.
        return;
      }

      // need to change this to majority from random choice.
      Player target = targets.get((int) (Math.random() * targets.size()));
      if (!isProtected(target)) {
        for (Player p : getPlayers(Role.WOLF)) {
          killMap.put(target, p);
        }
      }
    }

    for (Player p : getPlayers(Role.VIGILANTE)) {
      Player target = ((Vigilante) p.getRole()).getTarget();
      if (target != null && !isProtected(target)) {
        killMap.put(target, p);
      }
    }

    for (Player p : getPlayers(Role.DEMON)) {
      Player target = ((Demon) p.getRole()).getTarget();
      if (target != null && !isProtected(target)) {
        killMap.put(target, p);
      }
    }

    // Kill anyone who targets the demon - this ignores protection. May want to settings this later.
    for (Player demon : getPlayers(Role.DEMON)) {
      boolean wolfTarget = false;
      for (Player p : getPlayers()) {
        if (p.getRole().getTarget() == demon) {
          if ((p.getRole().getType() == Role.WOLF)) {
            wolfTarget = true;
          } else {
            killMap.put(p, demon);
          }
        }
      }
      if (wolfTarget) {
        List<Player> wolves = getPlayers(Role.WOLF);
        Player randomWolf = wolves.get((int) (Math.random() * wolves.size()));
        killMap.put(randomWolf, demon);
      }
    }

    // killMap should now have anyone who needs to be killed in it.

    for (Player player : getPlayers()) {
      player.getRole().onNightEnds();
    }

    getBot().sendMessage("The sun dawns upon the village.");
    if (!killMap.isEmpty()) {
      deathNotifications(killMap);

      if (config.getSettings().get("REVEAL_NIGHT_KILLERS").equals("YES")) {
        for (Player p : killMap.keySet()) {
          StringBuilder output = new StringBuilder();
          Player wolfKiller = null;
          output.append("You find that ").append(p.getName()).append(" ");
          for (Player killer : killMap.get(p)) {
            if (killer.getRole().getType() == Role.WOLF) {
              wolfKiller = killer;
            } else {
              output.append(killer.getRole().getKillMessage()).append(" and ");
            }
          }
          if(wolfKiller != null) {
            output.append(wolfKiller.getRole().getKillMessage());
          } else {
            output.setLength(output.length() - 5);
          }
          output.append(".");
          getBot().sendMessage(output.toString());
          p.setAlive(false);
        }
      } else if (config.getSettings().get("REVEAL_NIGHT_KILLERS").equals("NO")) {
        for (Player p : killMap.keySet()) {
          p.setAlive(false);
        }
        StringBuilder output = new StringBuilder();
        output.append("You find that ").append(Joiner.on(" and ").join(killMap.keySet()));
        if (killMap.keySet().size() > 1) {
          output.append(" are dead.");
        } else {
          output.append(" is dead.");
        }
        getBot().sendMessage(output.toString());
      }
    } else {
      getBot().sendMessage(NONE_DEAD_MSG);
    }

    String mode = config.getSettings().get("NIGHT_KILL_ANNOUNCE");
    if (!mode.equals("NONE")) {
      for (Player p : killMap.keySet()) {
        if (mode.equals("FACTION")) {
          getBot().sendMessage(p.getName() + " was a " + p.getRole().getFaction() + ".");
        } else if (mode.equals("ROLE")) {
          getBot().sendMessage(p.getName() + " was a " + p.getRole().getType() + ".");
        }
      }
    }

    getBot().onPlayersChanged();

    if (checkForWinner() != null) {
      return;
    }

    daytime = true;
    unmutePlayers();
  }

  private void deathNotifications(Multimap<Player, Player> killMap) {

    for (Player dead : killMap.keySet()) {
      for (Player killer : killMap.get(dead)) {
        String mode = null;
        if (killer.getRole().getType() == Role.DEMON) {
          mode = config.getSettings().get("TELL_DEMON_ON_KILL");
        } else if (killer.getRole().getType() == Role.WOLF) {
          mode = config.getSettings().get("TELL_WOLVES_ON_KILL");
        } else if (killer.getRole().getType() == Role.VIGILANTE) {
          mode = config.getSettings().get("TELL_VIGILANTE_ON_KILL");
        }
        if (mode != null) {
          if (mode.equals("FACTION")) {
            getBot().sendMessage(killer.getName(),
                dead.getName() + " was a " + dead.getRole().getFaction() + ".");
          } else if (mode.equals("ROLE")) {
            getBot().sendMessage(killer.getName(),
                dead.getName() + " was a " + dead.getRole().getType() + ".");
          } else if (mode.equals("NONE")) {}
        }
      }
    }
  }

  private boolean isProtected(Player player) {
    if (player.getRole().getType() == Role.DEMON) {
      return true;
    }
    for (Player p : getPlayers(Role.PRIEST)) {
      Priest priest = (Priest) p.getRole();
      if (Objects.equal(priest.getTarget(), player)) {
        return true;
      }
    }
    return false;
  }

  public void moveToNight() {
    daytime = false;

    getBot().muteAll();

    server.clearAllRooms();

    getBot().sendMessage("Night falls on the village.");

    for (Player player : getPlayers()) {
      player.getRole().onNightBegins();
    }
  }

  /**
   * @return The winning faction.
   */
  public Faction checkForWinner() {
    Map<Faction, Integer> factionCount = getFactionCounts();

    int numAlive = getPlayers().size();

    Faction winner = null;

    if (factionCount.get(Faction.VILLAGERS) == numAlive) {
      winner = Faction.VILLAGERS;
    } else if (factionCount.get(Faction.DEMONS) == 0) {
      if (factionCount.get(Faction.WOLVES) >= factionCount.get(Faction.VILLAGERS)) {
        if (getPlayers(Role.HUNTER).isEmpty()) {
          winner = Faction.WOLVES;
        } else {
          winner = Faction.VILLAGERS;
        }
      }
    } else {
      if (factionCount.get(Faction.DEMONS) >= Math.ceil(((double) numAlive) / 2)) {
        winner = Faction.DEMONS;
      }
    }

    if (winner != null) {
      getBot().sendMessage("The " + winner.getPluralForm() + " have won the game!");
      GameSummary.printGameLog(getBot(), players, winner);
      getBot().setStage(new InitialStage(getBot()));
      getBot().unmuteAll();
      getBot().recordGameResults(this);
    }

    return winner;
  }

  private Map<Faction, Integer> getFactionCounts() {
    Map<Faction, Integer> ret = Maps.newHashMap();

    for (Faction f : Faction.values()) {
      ret.put(f, 0);
    }

    for (Player p : getPlayers()) {
      Integer c = ret.get(p.getRole().getFaction());
      ret.put(p.getRole().getFaction(), c + 1);
    }

    return ret;
  }

  public Map<Player, Player> getVotesToDayKill() {
    return votesToDayKill;
  }

  /**
   * Gets an ALIVE player with the given name.
   */
  @Override
  public Player getPlayer(String name) {
    for (Player p : this.players) {
      if (p.getName().equalsIgnoreCase(name)) {
        if (p.isAlive()) {
          return p;
        } else {
          throw new WolfException(name + " is dead.");
        }
      }
    }

    throw new WolfException("No such player: " + name);
  }

  public List<Player> getPlayers(Role role) {
    List<Player> ret = Lists.newArrayList();
    for (Player player : getPlayers()) {
      if (player.getRole().getType() == role) {
        ret.add(player);
      }
    }
    return ret;
  }

  public List<Player> getPlayers(Faction faction) {
    List<Player> ret = Lists.newArrayList();
    for (Player player : getPlayers()) {
      if (player.getRole().getFaction() == faction) {
        ret.add(player);
      }
    }
    return ret;
  }

  /**
   * Returns every ALIVE player.
   */
  public Set<Player> getPlayers() {
    return ImmutableSortedSet.copyOf(filter(players, alive));
  }

  public VotingHistory getVotingHistory() {
    return votingHistory;
  }

  public boolean isDay() {
    return daytime;
  }

  public boolean isNight() {
    return !daytime;
  }

  @Override
  public List<Action> getAvailableActions(Player player) {

    List<Action> actions = Lists.newArrayList();

    if (player.isAdmin()) {
      actions.addAll(adminActions);
    }
    if (isDay()) {
      actions.addAll(daytimeActions);
      actions.addAll(chatActions);
    } else {
      List<Action> ret = Lists.newArrayList();
      ret.add(commandsAction);
      ret.addAll(player.getRole().getNightActions());
      actions.addAll(ret);
    }
    return actions;
  }

  @Override
  public void handleChat(IBot bot, String sender, String message) {
    Player player = getPlayer(sender);

    player.getRole().handleChat(player, message);
  }

  public String getSetting(String settingName) {
    return config.getSettings().get(settingName);
  }

  @Override
  public Iterable<Player> getAllPlayers() {
    return ImmutableList.copyOf(this.players);
  }

  public DateTime getStartDate() {
    return startDate;
  }

  public GameConfig getConfig() {
    return config;
  }

  public UUID getId() {
    return id;
  }

  private static final Predicate<Player> alive = new Predicate<Player>() {
    @Override
    public boolean apply(Player player) {
      return player.isAlive();
    }
  };

}
