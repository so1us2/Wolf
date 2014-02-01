package wolf.model.stage;

import java.util.List;
import java.util.Set;

import wolf.model.GameConfig;
import wolf.model.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import wolf.action.Action;
import wolf.action.setup.CommandsAction;
import wolf.action.setup.JoinAction;
import wolf.action.setup.LeaveAction;
import wolf.action.setup.ListConfigsAction;
import wolf.action.setup.ListPlayersAction;
import wolf.action.setup.ListRolesAction;
import wolf.action.setup.LoadConfigAction;
import wolf.action.setup.SetRoleAction;
import wolf.action.setup.StartGameAction;
import wolf.bot.IBot;

public class SetupStage extends Stage {

  private final List<Action> actions = Lists.newArrayList();
  private final Set<Player> players = Sets.newLinkedHashSet();
  private final GameConfig config = new GameConfig();

  public SetupStage(IBot bot) {
    super(bot);

    actions.add(new JoinAction(this));
    actions.add(new CommandsAction(this));
    actions.add(new LeaveAction(this));
    actions.add(new LoadConfigAction(this));
    actions.add(new ListConfigsAction(this));
    actions.add(new ListPlayersAction(this));
    actions.add(new ListRolesAction(this));
    actions.add(new SetRoleAction(this));
    actions.add(new StartGameAction(this));
  }

  public Set<Player> getPlayers() {
    return players;
  }

  public int getPlayersNeeded() {
    int n = 0;
    for (Integer i : config.getRoles().values()) {
      n += i;
    }
    return n;
  }

  public GameConfig getConfig() {
    return config;
  }
  
  @Override
  public List<Action> getAvailableActions() {
    return ImmutableList.copyOf(actions);
  }

}
