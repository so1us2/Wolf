package wolf.model.stage;

import java.util.List;
import java.util.Set;

import wolf.action.Action;
import wolf.action.Visibility;
import wolf.bot.IBot;
import wolf.model.Player;

import com.google.common.collect.ImmutableSet;

public abstract class Stage {

  private static final Set<String> admins = ImmutableSet.of("satnam", "semisober");

  private final IBot bot;

  public Stage(IBot bot) {
    this.bot = bot;
  }

  public void handleChat(IBot bot, String sender, String message, boolean isPrivate) {}

  public void handle(IBot bot, String sender, String command, List<String> args, boolean isPrivate) {
    Player player = getPlayer(sender);

    Action action = getActionForCommand(command, player);

    if (action == null) {
      String message = "Invalid command: " + command;
      if (isPrivate) {
        bot.sendMessage(sender, message);
      } else {
        bot.sendMessage(message);
      }
      action = getActionForCommand("commands", player);
    }

    if (isPrivate && action.getVisibility() == Visibility.PUBLIC) {
      bot.sendMessage(sender, "The " + command + " action does not work as a private message.");
    } else if (!isPrivate && action.getVisibility() == Visibility.PRIVATE) {
      bot.sendMessage(sender, "The " + command + " should be sent as a private message.");
    }

    action.apply(player, args, isPrivate);
  }

  public Player getPlayer(String name) {
    return new Player(name, admins.contains(name));
  }

  private Action getActionForCommand(String command, Player player) {
    for (Action a : getAvailableActions(player)) {
      if (a.getName().equalsIgnoreCase(command)) {
        return a;
      }
    }
    return null;
  }

  public abstract List<Action> getAvailableActions(Player player);

  public IBot getBot() {
    return bot;
  }

}
