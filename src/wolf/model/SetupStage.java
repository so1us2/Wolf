package wolf.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import wolf.action.Action;
import wolf.action.setup.CommandsAction;
import wolf.action.setup.InitGameAction;
import wolf.action.setup.JoinAction;
import wolf.action.setup.LeaveAction;
import wolf.action.setup.ListConfigsAction;
import wolf.action.setup.LoadConfigAction;
import wolf.bot.NarratorBot;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class SetupStage extends Stage {

  private final List<Action> actions = Lists.newArrayList();
  private final Set<Player> players = Sets.newLinkedHashSet();
  private final GameConfig config = new GameConfig();

  public SetupStage(NarratorBot bot) {
    super(bot);

    actions.add(new JoinAction(this));
    actions.add(new CommandsAction(this));
    actions.add(new InitGameAction());
    actions.add(new LeaveAction(this));
    actions.add(new LoadConfigAction(this));
    actions.add(new ListConfigsAction(this));
  }

  public Set<Player> getPlayers() {
    return players;
  }

  public Map<Role, Integer> getRoles() {
    return config.getRoles();
  }

  public void setRole(Role role, int num) {
    config.setRole(role, num);
  }

  public void replaceRoles(Map<Role, Integer> newRoles) {
    config.replaceRoles(newRoles);
  }

  @Override
  public List<Action> getAvailableActions() {
    return ImmutableList.copyOf(actions);
  }

}
