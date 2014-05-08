package wolf.model.stage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import wolf.action.Action;
import wolf.bot.IBot;
import wolf.model.Player;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public abstract class Stage {

  public static final Set<String> admins = new HashSet<String>(ImmutableSet.of("satnam", "TomM",
      "Lauren", "LeeSharpe"));

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
    if (admins.contains(sender)) {
      Action adminAction = getAdminAction(command);
      if (adminAction != null) {
        adminAction.apply(new Player(sender, true), args);
        return;
      }
    }
  
    Player player = getPlayer(sender);
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
    return new Player(name, admins.contains(name));
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

  private Action getAdminAction(String command) {
    for (Action a : getAdminActions()) {
      if (a.getName().equalsIgnoreCase(command)) {
        return a;
      }
    }
    return null;
  }

  public abstract List<Action> getAvailableActions(Player player);

  public List<Action> getAdminActions() {
    return ImmutableList.of();
  }

  public IBot getBot() {
    return bot;
  }

}
