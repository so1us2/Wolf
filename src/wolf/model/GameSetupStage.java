package wolf.model;

import java.util.List;
import java.util.Set;

import wolf.action.Action;
import wolf.action.setup.JoinAction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class GameSetupStage extends Stage {

  private final List<Action> actions = Lists.newArrayList();
  private final Set<Player> players = Sets.newLinkedHashSet();

  public GameSetupStage() {
    actions.add(new JoinAction(this));
  }

  public Set<Player> getPlayers() {
    return players;
  }

  @Override
  public List<Action> getAvailableActions() {
    return ImmutableList.copyOf(actions);
  }

}
