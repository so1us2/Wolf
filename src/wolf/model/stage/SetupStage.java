package wolf.model.stage;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import wolf.action.Action;
import wolf.action.game.host.AbortGameAction;
import wolf.action.setup.CurrentSetupAction;
import wolf.action.setup.DetailsAction;
import wolf.action.setup.InviteAction;
import wolf.action.setup.JoinAction;
import wolf.action.setup.LeaveAction;
import wolf.action.setup.ListAllConfigsAction;
import wolf.action.setup.ListAllRolesAction;
import wolf.action.setup.ListPlayersAction;
import wolf.action.setup.ListSettingsAction;
import wolf.action.setup.PrivateAction;
import wolf.action.setup.host.AppointHostAction;
import wolf.action.setup.host.KickPlayerAction;
import wolf.action.setup.host.LoadConfigAction;
import wolf.action.setup.host.SetFlagAction;
import wolf.action.setup.host.SetPlayersAction;
import wolf.action.setup.host.SetRoleAction;
import wolf.action.setup.host.StartGameAction;
import wolf.bot.IBot;
import wolf.model.GameConfig;
import wolf.model.Player;

public class SetupStage extends Stage {

  private final Set<Action> actions = Sets.newTreeSet();
  private final Set<Player> players = Sets.newLinkedHashSet();
  private final Set<Action> hostActions = Sets.newTreeSet();
  private final GameConfig config = new GameConfig();
  private final Set<String> inviteList = Sets.newCopyOnWriteArraySet();

  private boolean privateGame = false;

  public SetupStage(IBot bot) {
    super(bot);

    hostActions.add(new AppointHostAction(this));
    hostActions.add(new KickPlayerAction(this));
    hostActions.add(new StartGameAction(this));
    hostActions.add(new LoadConfigAction(this));
    hostActions.add(new SetRoleAction(this));
    hostActions.add(new SetFlagAction(this));
    hostActions.add(new AbortGameAction(this));

    actions.add(new JoinAction(this));
    actions.add(new LeaveAction(this));
    actions.add(new ListAllConfigsAction(this));
    actions.add(new ListPlayersAction(this));
    actions.add(new CurrentSetupAction(this));
    actions.add(new DetailsAction(this));
    actions.add(new ListAllRolesAction(this));
    actions.add(new ListSettingsAction(this));
    actions.add(new SetPlayersAction(this));
    actions.add(new PrivateAction(this));
    actions.add(new InviteAction(this));
  }

  @Override
  public Player getPlayerOrNull(String name) {
    for (Player p : players) {
      if (p.getName().equalsIgnoreCase(name)) {
        return p;
      }
    }
    return null;
  }

  @Override
  public void setHost(Player newHost) {
    config.setHost(newHost);
    if (config.getHost() == null) {
      getBot().sendMessage("There is now no host.");
    } else {
      getBot().sendMessage(config.getHost().getName() + " is now the host of the game.");
    }
  }

  @Override
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
      getBot().onPlayersChanged();
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
  public List<Action> getStageActions(Player player) {
    List<Action> playerActions = Lists.newArrayList();
    if (player.equals(config.getHost()) || player.isAdmin()) {
      playerActions.addAll(hostActions);
    }
    playerActions.addAll(actions);
    return playerActions;
  }

  @Override
  public Iterable<Player> getAllPlayers() {
    return ImmutableSet.copyOf(players);
  }

  @Override
  public int getStageIndex() {
    return 1;
  }

  public void setPrivate(boolean b) {
    this.privateGame = b;
  }

  public boolean isPrivateGame() {
    return privateGame;
  }

  public Set<String> getInviteList() {
    return inviteList;
  }

}
