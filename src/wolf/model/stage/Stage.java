package wolf.model.stage;

import java.util.List;

import org.testng.collections.Lists;

import wolf.action.Action;
import wolf.action.global.GetHelpAction;
import wolf.action.global.ReportAction;
import wolf.action.global.RollAction;
import wolf.bot.IBot;
import wolf.model.Player;

public abstract class Stage {

  private final IBot bot;

  public Stage(IBot bot) {
    this.bot = bot;
  }

  public Player getHost() {
    return null;
  }

  public void setHost(Player p) {}

  public void handleChat(IBot bot, String sender, String message) {
    bot.sendToAll(sender, message);
  }

  public void handle(IBot bot, String sender, String command, List<String> args) {
    Player player = getPlayerOrNull(sender);
    if (player == null) {
      player = new Player(sender, bot.isAdmin(sender));
      player.setAlive(!(this instanceof GameStage));
    }

    Action action = getActionForCommand(command, player);

    if (action == null) {
      String message = "Invalid command: " + command;
      bot.sendMessage(sender, message);
      action = getActionForCommand("help", player);
    }

    action.apply(player, args);
  }

  public abstract Iterable<Player> getAllPlayers();

  public Player getPlayer(String name) {
    return new Player(name, getBot().isAdmin(name));
  }

  public Player getPlayerOrNull(String name) {
    return getPlayer(name);
  }

  private Action getActionForCommand(String command, Player player) {
    for (Action a : getAvailableActions(player)) {
      if (a.getName().equalsIgnoreCase(command)) {
        return a;
      }
    }
    return null;
  }

  public final List<Action> getAvailableActions(Player player) {
    List<Action> ret = Lists.newArrayList();
    ret.add(new GetHelpAction(this));
    ret.add(new ReportAction(this));
    ret.add(new RollAction(this));
    ret.addAll(getStageActions(player));
    return ret;
  }

  protected abstract List<Action> getStageActions(Player player);

  public IBot getBot() {
    return bot;
  }

  public void onAbort() {}

  public abstract int getStageIndex();

}
