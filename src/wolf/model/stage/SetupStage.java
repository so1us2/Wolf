package wolf.model.stage;

import java.util.List;
import java.util.Set;

import wolf.action.Action;
import wolf.action.GetHelpAction;
import wolf.action.setup.CurrentSetupAction;
import wolf.action.setup.DetailsAction;
import wolf.action.setup.JoinAction;
import wolf.action.setup.LeaveAction;
import wolf.action.setup.ListAllConfigsAction;
import wolf.action.setup.ListAllRolesAction;
import wolf.action.setup.ListPlayersAction;
import wolf.action.setup.ListSettingsAction;
import wolf.action.setup.host.LoadConfigAction;
import wolf.action.setup.host.SetFlagAction;
import wolf.action.setup.host.SetRoleAction;
import wolf.action.setup.host.StartGameAction;
import wolf.bot.IBot;
import wolf.model.GameConfig;
import wolf.model.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class SetupStage extends Stage {

  private final Set<Action> actions = Sets.newTreeSet();
  private final Set<Player> players = Sets.newLinkedHashSet();
  private final GameConfig config = new GameConfig();

  public SetupStage(IBot bot) {
    super(bot);

    actions.add(new JoinAction(this));
    actions.add(new GetHelpAction(this));
    actions.add(new LeaveAction(this));
    actions.add(new LoadConfigAction(this));
    actions.add(new ListAllConfigsAction(this));
    actions.add(new ListPlayersAction(this));
    actions.add(new CurrentSetupAction(this));
    actions.add(new SetRoleAction(this));
    actions.add(new StartGameAction(this));
    actions.add(new DetailsAction(this));
    actions.add(new ListAllRolesAction(this));
    actions.add(new ListSettingsAction(this));
    actions.add(new SetFlagAction(this));
  }

  public void setHost(Player newHost) {
    config.setHost(newHost);
    if (config.getHost() == null) {
      getBot().sendMessage("There is now no host.");
    } else {
      getBot().sendMessage(config.getHost().getName() + " is now the host of the game.");
    }
  }

  public Player getHost() {
    return config.getHost();
  }

  public Set<Player> getPlayers() {
    return players;
  }

  public GameConfig getConfig() {
    return config;
  }

  public boolean removePlayer(Player p) {
    if (!players.remove(p)) {
      return false;
    }
    getBot().sendMessage(p.getName() + " left the game (" + getPlayers().size() + " players)");
    if (players.isEmpty()) {
      setHost(null);
      return true;
    } else if (p.equals(config.getHost())) {
      Set<Player> players = getPlayers();
      setHost(Iterables.get(players, (int) (Math.random() * players.size())));
    }
    getBot().onPlayersChanged();
    return true;
  }

  public boolean addPlayer(Player p) {
    if (!players.add(p)) {
      return false;
    }
    getBot().sendMessage(p.getName() + " joined the game (" + getPlayers().size() + " players)");
    if (config.getHost() == null) {
      setHost(p);
    }
    getBot().onPlayersChanged();
    return true;
  }

  @Override
  public List<Action> getAvailableActions(Player player) {
    return ImmutableList.copyOf(actions);
  }

  @Override
  public Iterable<Player> getAllPlayers() {
    return ImmutableSet.copyOf(players);
  }

}
