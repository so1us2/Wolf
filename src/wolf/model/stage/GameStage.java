package wolf.model.stage;

import static com.google.common.collect.Iterables.filter;

import java.util.List;
import java.util.Map;
import java.util.Set;

import wolf.WolfException;
import wolf.action.Action;
import wolf.action.game.ListPlayersAction;
import wolf.action.game.VoteAction;
import wolf.action.game.VoteCountAction;
import wolf.action.setup.CommandsAction;
import wolf.bot.IBot;
import wolf.model.Faction;
import wolf.model.GameConfig;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.VotingHistory;
import wolf.model.role.Bartender;
import wolf.model.role.Priest;
import wolf.model.role.Vigilante;
import wolf.model.role.Wolf;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GameStage extends Stage {

  public static final String NONE_DEAD_MSG =
      "The sun dawns and the village finds that no one has died in the night.";

  private final CommandsAction commandsAction = new CommandsAction(this);
  private final List<Action> daytimeActions = Lists.newArrayList();
  private final VotingHistory votingHistory = new VotingHistory();
  private final Map<Player, Player> votesToDayKill = Maps.newLinkedHashMap();

  /**
   * The set of all players (even dead ones).
   */
  private final Set<Player> players;

  private boolean daytime = true;

  public GameStage(IBot bot, GameConfig config, Set<Player> players) {
    super(bot);

    this.players = ImmutableSortedSet.copyOf(players);

    daytimeActions.add(commandsAction);
    daytimeActions.add(new VoteAction(this));
    daytimeActions.add(new VoteCountAction(this));
    daytimeActions.add(new ListPlayersAction(this));

    for (Player player : players) {
      player.getRole().setStage(this);
    }

    beginGame();
  }

  private void beginGame() {
    // tell the wolves who they are
    List<Player> wolves = getPlayers(Role.WOLF);
    for (Player wolf : wolves) {
      getBot().sendMessage(wolf.getName(), "The wolves are: " + wolves);
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
  public void handle(IBot bot, String sender, String command, List<String> args, boolean isPrivate) {
    super.handle(bot, sender, command, args, isPrivate);

    if (isNight()) {
      checkForEndOfNight();
    }
  }

  private void checkForEndOfNight() {
    for (Player player : players) {
      if (!player.getRole().isFinishedWithNightAction()) {
        return;
      }
    }

    // bring night to a close.
    moveToDay();
  }

  private void moveToDay() {

    Set<Player> dying = Sets.newHashSet();
    List<Player> targets = Lists.newArrayList();

    for (Player p : getPlayers(Role.WOLF)) {
      Wolf wolf = (Wolf) p.getRole();
      targets.add(wolf.getKillTarget());
    }

    if (targets.contains(null)) {
      // wolves haven't finished choosing yet.
      return;
    }

    // Get anyone who needs to die into the dying set.

    // need to change this to majority from random choice.
    Player target = targets.get((int) (Math.random() * targets.size()));

    // Handle wolf kill.
    if (!isProtected(target)) {
      dying.add(target);
    }

      // this is not the default mode.
      // getBot().sendMessage(
      // "The sun dawns and the village awakens to find the ripped-apart corpse of "
      // + target.getName() + ".");

    for (Player p : getPlayers(Role.VIGILANTE)) {
      Vigilante vig = (Vigilante) p.getRole();
      if (vig.getKillTarget() != null && !isProtected(vig.getKillTarget())) {
        dying.add(vig.getKillTarget());
      }
    }

    // Dying set should now have anyone who needs to be killed in it.

    for (Player player : getPlayers()) {
      player.getRole().onNightEnds(player);
    }

    if (!dying.isEmpty()) {
      StringBuilder output = new StringBuilder();
      output.append("The sun dawns and you find ");
      for (Player p : dying) {
        output.append(p.getName()).append(" and ");
        p.setAlive(false);
      }
      output.setLength(output.length() - 4);
      output.append("dead in the village.");
      getBot().sendMessage(output.toString());
    } else {
      getBot().sendMessage(NONE_DEAD_MSG);
    }

    for (Player p : getPlayers(Role.BARTENDER)) {
      Bartender b = (Bartender) p.getRole();
      if (b.getDrinkTarget() != null) {
        getBot().sendMessage(b.getDrinkTarget() + " has a drink waiting for them.");
      }
    }

    if (checkForWinner() != null) {
      return;
    }

    daytime = true;
    unmutePlayers();
  }

  private boolean isProtected(Player player) {
    for (Player p : getPlayers(Role.PRIEST)) {
      Priest priest = (Priest) p.getRole();
      if (Objects.equal(priest.getProtectTarget(), player)) {
        return true;
      }
    }
    return false;
  }

  public void moveToNight() {
    daytime = false;

    getBot().muteAll();

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
    } else if (factionCount.get(Faction.WOLVES) >= factionCount.get(Faction.VILLAGERS)) {
      winner = Faction.WOLVES;
    }

    if (winner != null) {
      getBot().sendMessage("The " + winner.getPluralForm() + " have won the game!");
      getBot().setStage(new InitialStage(getBot()));
      getBot().unmuteAll();
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
    if (isDay()) {
      return daytimeActions;
    } else {
      List<Action> ret = Lists.newArrayList();
      ret.add(commandsAction);
      ret.addAll(player.getRole().getNightActions());
      return ret;
    }
  }

  @Override
  public void handleChat(IBot bot, String sender, String message, boolean isPrivate) {
    Player player = getPlayer(sender);

    player.getRole().handleChat(player, message, isPrivate);
  }

  private static final Predicate<Player> alive = new Predicate<Player>() {
    @Override
    public boolean apply(Player player) {
      return player.isAlive();
    }
  };

}
