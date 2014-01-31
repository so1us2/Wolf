package wolf.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import wolf.WolfException;
import wolf.action.Action;
import wolf.action.game.VoteAction;
import wolf.bot.IBot;

public class GameStage extends Stage {

  private final List<Action> actions = Lists.newArrayList();

  private final Map<Player, Player> votesToLynch = Maps.newLinkedHashMap();

  private final Set<Player> players;

  public GameStage(IBot bot, GameConfig config, Set<Player> players) {
    super(bot);

    this.players = ImmutableSet.copyOf(players);

    actions.add(new VoteAction(this));
  }

  @Override
  public List<Action> getAvailableActions() {
    return ImmutableList.copyOf(actions);
  }

  public Map<Player, Player> getVotesToLynch() {
    return votesToLynch;
  }

  public Player getPlayer(String name) {
    for (Player p : players) {
      if (p.getName().equalsIgnoreCase(name)) {
        return p;
      }
    }
    throw new WolfException("No such player: " + name);
  }

}
