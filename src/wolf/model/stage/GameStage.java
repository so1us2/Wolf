package wolf.model.stage;

import static com.google.common.collect.Iterables.filter;
import static java.lang.Integer.parseInt;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import wolf.ChatLogger;
import wolf.WolfException;
import wolf.action.Action;
import wolf.action.game.ClearVoteAction;
import wolf.action.game.GetRoleAction;
import wolf.action.game.ListPlayersAction;
import wolf.action.game.VoteAction;
import wolf.action.game.VoteCountAction;
import wolf.action.game.admin.GetVotersAction;
import wolf.action.game.admin.ModkillPlayerAction;
import wolf.action.game.host.AbortGameAction;
import wolf.action.game.host.AnnounceAction;
import wolf.action.game.host.ReminderAction;
import wolf.action.privatechats.AuthorizePlayerAction;
import wolf.action.privatechats.ChatAction;
import wolf.action.privatechats.JoinRoomAction;
import wolf.action.privatechats.LeaveRoomAction;
import wolf.action.privatechats.ListRoomsAction;
import wolf.action.privatechats.NewRoomAction;
import wolf.action.privatechats.RevokeAuthorizationAction;
import wolf.action.setup.CurrentSetupAction;
import wolf.bot.IBot;
import wolf.model.Faction;
import wolf.model.GameConfig;
import wolf.model.GameSummary;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.VotingHistory;
import wolf.model.chat.ChatServer;
import wolf.model.role.AbstractWolfRole;
import wolf.model.role.Corrupter;
import wolf.model.role.Demon;
import wolf.model.role.Priest;
import wolf.model.role.Vigilante;
import wolf.web.GameRoom;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;

public class GameStage extends Stage {

  public static final String NONE_DEAD_MSG =
      "The sun dawns and the village finds that no one has died in the night.";

  private final UUID id = UUID.randomUUID();

  private final List<Action> daytimeActions = Lists.newArrayList();

  private final List<Action> hostActions = Lists.newArrayList();
  private final List<Action> adminActions = Lists.newArrayList();

  private final List<Action> chatActions = Lists.newArrayList();

  private final VotingHistory votingHistory = new VotingHistory();

  private final Map<Player, Player> votesToDayKill = Maps.newLinkedHashMap();
  private final List<Multimap<Player, Player>> killHistory = Lists.newArrayList();
  private ChatServer server;

  private boolean gameRunning = true;

  /**
   * The set of all players (even dead ones).
   */
  private final Set<Player> players;

  private boolean daytime = true;
  private boolean announcedTime = false;

  private final GameConfig config;

  /**
   * This is stored as part of the GameHistory.
   */
  private final LocalDateTime startDate = LocalDateTime.now();

  private int minutesPerRound;
  private LocalDateTime roundEndTime;

  private final ScheduledExecutorService executorService;

  private final ChatLogger logger;

  private int day = 1;

  public GameStage(IBot bot, GameConfig config, Set<Player> players) {
    super(bot);

    this.config = config;
    this.players = ImmutableSortedSet.copyOf(players);
    this.minutesPerRound = parseInt(config.getSettings().get("TIME_LIMIT"));
    this.roundEndTime = LocalDateTime.now().plusMinutes(minutesPerRound);

    server = new ChatServer(bot);

    logger = new ChatLogger(id, config, this.players);
    bot.setLogger(logger);

    daytimeActions.add(new VoteAction(this));
    daytimeActions.add(new VoteCountAction(this));
    if (config.getSettings().get("WITHDRAW_VOTES").equals("YES")) {
      daytimeActions.add(new ClearVoteAction(this));
    }
    daytimeActions.add(new ListPlayersAction(this));
    daytimeActions.add(new GetRoleAction(this));
    daytimeActions.add(new CurrentSetupAction(this));

    adminActions.add(new ModkillPlayerAction(this));
    adminActions.add(new GetVotersAction(this));

    hostActions.add(new AnnounceAction(this));
    hostActions.add(new AbortGameAction(this));
    hostActions.add(new ReminderAction(this));

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

    executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleAtFixedRate(checkTimer, 0, 1, TimeUnit.SECONDS);

    try {
      beginGame();
    } catch (Exception e) {
      e.printStackTrace();
      bot.sendMessage("There was a server error when initializing the game!");
      bot.setStage(new InitialStage(getBot()));
      return;
    }
  }
  
  private Runnable checkTimer = new Runnable() {
    @Override
    public void run() {
      if (!daytime || !gameRunning) {
        return;
      }

      if (!announcedTime && LocalDateTime.now().plusMinutes(1).isAfter(roundEndTime)) {
        announcedTime = true;
        getBot().sendMessage("The day is almost at an end! You have 60 seconds left to vote.");
      }

      if (LocalDateTime.now().isAfter(roundEndTime)) {
        synchronized (GameStage.this) {
          getBot().sendToAll(GameRoom.NARRATOR, "The day has come to an end.");
          VoteAction.processVotes(getBot(), GameStage.this, getVotesToDayKill(), true);
        }
      }
    }
  };

  private void beginGame() {
    getBot().sendMessage(
        "If this is your first game, please read the rules link "
            + "up above. You can use /status to see what roles are in the game.");
    getBot().sendMessage(
        "Please do NOT copy/paste any text from the moderator (bold and purple) as it is private.");
    getBot().sendMessage(
        "Also, please do not restate anything sent you by the moderator to prove a role. "
            + "That compromises the game.");
    getBot().sendMessage("You can use the command /help at any time for more assistance.");

    for (Player player : getPlayers()) {
      player.getRole().onGameStart();
    }

    unmutePlayers();

    getBot().sendMessage("Day 1 dawns on the village.");
    sendTimeToAll();
  }

  @Override
  public synchronized void handle(IBot bot, String sender, String command, List<String> args) {
    logger.command(sender, command, args);

    super.handle(bot, sender, command, args);

    if (isNight()) {
      checkForEndOfNight();
    }
  }

  @Override
  public synchronized void handleChat(IBot bot, String sender, String message) {
    if (isSilentGame()) {
      return;
    }

    logger.chat(sender, message);

    Player player = getPlayer(sender);
    player.getRole().handleChat(player, message);
  }

  private boolean isSilentGame() {
    return "YES".equals(getSetting("SILENT_GAME"));
  }

  private void unmutePlayers() {
    for (Player player : getPlayers()) {
      getBot().unmute(player.getName());
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
    // Keys are victims, Values are killers.
    Multimap<Player, Player> killMap = TreeMultimap.create();

    if (!getPlayers(Faction.WOLVES).isEmpty()) {
      List<Player> targets = Lists.newArrayList();
      for (Player p : getPlayers(Faction.WOLVES)) {
        AbstractWolfRole wolf = (AbstractWolfRole) p.getRole();
        targets.add(wolf.getKillTarget());
      }

      if (targets.contains(null)) {
        // wolves haven't finished choosing yet.
        return;
      }

      // need to change this to majority from random choice.
      Player target = targets.get((int) (Math.random() * targets.size()));
      if (!isProtected(target)) {
        for (Player p : getPlayers(Faction.WOLVES)) {
          killMap.put(target, p);
        }
      }
    }

    for (Player p : getPlayers(Role.VIGILANTE)) {
      Vigilante vig = (Vigilante) p.getRole();
      if (isCorrupterTarget(p)) {
        vig.corrupt();
      }
      Player target = vig.getKillTarget();
      if (target != null) {
        if (isProtected(target)) {
          getBot().sendMessage(p.getName(), "Your bullet bounces off of " + target.getName() + ".");
        } else {
          killMap.put(target, p);
          getBot().sendMessage(p.getName(),
              "You shoot " + target.getName() + " square between the eyes.");
        }
      }
    }

    for (Player p : getPlayers(Role.DEMON)) {
      Player target = ((Demon) p.getRole()).getKillTarget();
      if (target != null && !isProtected(target)) {
        killMap.put(target, p);
      }
    }

    // Kill anyone who targets the demon - this ignores protection. May want to settings this later.
    // Clean this up later to not loop through demons.
    for (Player demon : getPlayers(Role.DEMON)) {
      boolean wolfTarget = false;
      for (Player p : getPlayers()) {
        if (p.getRole().getKillTarget() == demon || p.getRole().getSpecialTarget() == demon) {
          if ((p.getRole().getType() == Role.WOLF)) {
            wolfTarget = true;
          } else {
            getBot()
                .sendMessage(p.getName(),
                    "You realize with horror that you've targeted a demon as your soul bleeds from your body.");
            killMap.put(p, demon);
          }
        }
      }
      if (wolfTarget) {
        List<Player> wolves = getPlayers(Role.WOLF);
        Player randomWolf = wolves.get((int) (Math.random() * wolves.size()));
        getBot()
            .sendMessage(randomWolf.getName(),
                "You realize with horror that you've targeted a demon as your soul bleeds from your body.");
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
          if (wolfKiller != null) {
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

    killHistory.add(killMap);
    getBot().onPlayersChanged();

    if (checkForWinner() != null) {
      return;
    }

    daytime = true;
    day++;
    announcedTime = false;
    roundEndTime = LocalDateTime.now().plusMinutes(minutesPerRound);
    sendTimeToAll();
    getBot().sendMessage("");
    getBot().sendMessage("*********************");
    getBot().sendMessage("NEW DAY");
    getBot().sendMessage("*********************");
    getBot().sendMessage("");
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
          mode = config.getSettings().get("TELL_VIG_ON_KILL");
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
      if (!isCorrupterTarget(p)) {
        Priest priest = (Priest) p.getRole();
        if (Objects.equal(priest.getSpecialTarget(), player)) {
          return true;
        }
      }
    }
    return false;
  }

  public void moveToNight() {
    daytime = false;

    getBot().muteAll();
    server.clearAllRooms();
    getBot().sendMessage("Night falls on the village.");
    sendTimeToAll();

    for (Player player : getPlayers()) {
      player.getRole().onNightBegins();
    }
  }

  /**
   * @return The winning faction.
   */
  public Faction checkForWinner() {
    Faction winner = null;

    if (daytime && day == 1) {
      // check for suicide villager win
      if (getDeadPlayers().size() == 1) {
        Player p = getDeadPlayers().iterator().next();
        if (p.getRole().getType() == Role.SUICIDE_VILLAGER) {
          winner = Faction.SUICIDE;
        }
      }
    }

    if (winner == null) {
      Map<Faction, Integer> factionCount = getFactionCounts();

      int numAlive = getPlayers().size();

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
    }

    if (winner != null) {
      gameRunning = false;
      sendTimeToAll();
      try {
        executorService.shutdownNow();
      } catch (Exception e) {
        e.printStackTrace();
      }

      getBot().sendMessage("<h2>The " + winner.getPluralForm() + " have won the game!</h2>");
      GameSummary.printGameLog(getBot(), players, winner, killHistory);
      getBot().setStage(new InitialStage(getBot()));
      getBot().unmuteAll();
      getBot().recordGameResults(this);
      logger.close();
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


  public void printVotes() {
    StringBuilder sb = new StringBuilder();
    sb.append("VOTES: ");
    if (votesToDayKill.isEmpty()) {
      sb.append("No votes.");
    } else {
      for (Player p : votesToDayKill.keySet()) {
        sb.append(p).append("-->").append(votesToDayKill.get(p)).append(", ");
      }
      sb.setLength(sb.length() - 2);
    }
    getBot().sendMessage(sb.toString());
  }

  /**
   * Gets an ALIVE player with the given name.
   */
  @Override
  public Player getPlayer(String name) {
    Player p = getPlayerOrNull(name);

    if (p == null) {
      throw new WolfException("No such player: " + name);
    }

    if (!p.isAlive()) {
      throw new WolfException(name + " is dead.");
    }

    return p;
  }

  /**
   * Gets the player with the given name (alive or dead).
   * 
   * Returns 'null' if that player doesn't exist.
   */
  @Override
  public Player getPlayerOrNull(String name) {
    for (Player p : this.players) {
      if (p.getName().equalsIgnoreCase(name)) {
        return p;
      }
    }
    return null;
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

  public Set<Player> getDeadPlayers() {
    return ImmutableSortedSet.copyOf(filter(players, Predicates.not(alive)));
  }

  public boolean isCorrupterTarget(Player target) {
    for (Player p : getPlayers(Role.CORRUPTER)) {
      Corrupter corrupter = (Corrupter) p.getRole();
      if (corrupter.getSpecialTarget().equals(target)) {
        return true;
      }
    }
    return false;
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
  public List<Action> getStageActions(Player player) {
    List<Action> actions = Lists.newArrayList();

    if (player.isAdmin()) {
      actions.addAll(hostActions);
      actions.addAll(adminActions);
    } else if (player.equals(config.getHost())) {
      actions.addAll(hostActions);
    }
    if (player.isAlive()) {
      if (isDay()) {
        if (config.getSettings().get("PRIVATE_CHAT").equals("ENABLED")) {
          actions.addAll(chatActions);
        }
        actions.addAll(daytimeActions);
      } else {
        List<Action> ret = Lists.newArrayList();
        ret.addAll(player.getRole().getNightActions());
        actions.addAll(ret);
      }
    }
    return actions;
  }

  public String getSetting(String settingName) {
    return config.getSettings().get(settingName);
  }

  @Override
  public Iterable<Player> getAllPlayers() {
    return ImmutableList.copyOf(this.players);
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public GameConfig getConfig() {
    return config;
  }

  @Override
  public void setHost(Player newHost) {
    config.setHost(newHost);
    if (config.getHost() == null) {
      getBot().sendMessage("There is now no host.");
    } else {
      getBot().sendMessage(config.getHost() + " is now the host of the game.");
    }
  }

  @Override
  public Player getHost() {
    return config.getHost();
  }

  public UUID getId() {
    return id;
  }

  @Override
  public void onAbort() {
    gameRunning = false;
    sendTimeToAll();
    executorService.shutdownNow();
    logger.close();
  }

  private void sendTimeToAll() {
    getBot().sendToAll("TIMER", "end", getEndTime());
  }

  public long getEndTime() {
    // this might be broken now
    return daytime && gameRunning ? roundEndTime.atZone(ZoneOffset.UTC).toEpochSecond() * 1000 : -1;
  }

  @Override
  public int getStageIndex() {
    return 2;
  }

  private static final Predicate<Player> alive = new Predicate<Player>() {
    @Override
    public boolean apply(Player player) {
      return player.isAlive();
    }
  };

}
