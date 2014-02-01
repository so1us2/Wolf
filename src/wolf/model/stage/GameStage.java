package wolf.model.stage;

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
import wolf.model.role.Priest;
import wolf.model.role.Wolf;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static com.google.common.collect.Iterables.filter;

public class GameStage extends Stage {

  public static final String NONE_DEAD_MSG = "The sun dawns and the village thanks the "
      + "gods that not a single person was killed this night.";

  private final CommandsAction commandsAction = new CommandsAction(this);
  private final List<Action> daytimeActions = Lists.newArrayList();
  private final VotingHistory votingHistory = new VotingHistory();
  private final Map<Player, Player> votesToLynch = Maps.newLinkedHashMap();

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
    List<Player> targets = Lists.newArrayList();
    for (Player p : getPlayers(Role.WOLF)) {
      Wolf wolf = (Wolf) p.getRole();
      targets.add(wolf.getKillTarget());
    }

    if (targets.contains(null)) {
      // wolves haven't finished choosing yet.
      return;
    }

    Player target = targets.get((int) (Math.random() * targets.size()));

    if (isProtected(target)) {
      getBot().sendMessage(NONE_DEAD_MSG);
    } else {
      target.setAlive(false);

      getBot().sendMessage(
          "The sun dawns and the village awakens to find the ripped-apart corpse of "
              + target.getName() + ".");

      if (checkForWinner() != null) {
        return;
      }
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

  public Map<Player, Player> getVotesToLynch() {
    return votesToLynch;
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
