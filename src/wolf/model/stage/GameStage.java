package wolf.model.stage;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import wolf.WolfException;
import wolf.action.Action;
import wolf.action.game.VoteAction;
import wolf.action.game.VoteCountAction;
import wolf.action.setup.CommandsAction;
import wolf.bot.IBot;
import wolf.model.GameConfig;
import wolf.model.Player;
import wolf.model.VotingHistory;

import static com.google.common.collect.Iterables.filter;

public class GameStage extends Stage {

  private final List<Action> actions = Lists.newArrayList();

  private final VotingHistory votingHistory = new VotingHistory();

  private final Map<Player, Player> votesToLynch = Maps.newLinkedHashMap();

  /**
   * The set of all players (even dead ones).
   */
  private final Set<Player> players;

  public GameStage(IBot bot, GameConfig config, Set<Player> players) {
    super(bot);

    this.players = ImmutableSet.copyOf(players);

    actions.add(new CommandsAction(this));
    actions.add(new VoteAction(this));
    actions.add(new VoteCountAction(this));
  }

  @Override
  public List<Action> getAvailableActions() {
    return ImmutableList.copyOf(actions);
  }

  public Map<Player, Player> getVotesToLynch() {
    return votesToLynch;
  }

  /**
   * Gets an ALIVE player with the given name.
   */
  @Override
  public Player getPlayer(String name) {
    for (Player p : getPlayers()) {
      if (p.getName().equalsIgnoreCase(name)) {
        return p;
      }
    }
    throw new WolfException("No such player: " + name);
  }

  /**
   * Returns every ALIVE player.
   */
  public Set<Player> getPlayers() {
    return ImmutableSet.copyOf(filter(players, alive));
  }

  public VotingHistory getVotingHistory() {
    return votingHistory;
  }

  private static final Predicate<Player> alive = new Predicate<Player>() {
    @Override
    public boolean apply(Player player) {
      return player.isAlive();
    }
  };

}
