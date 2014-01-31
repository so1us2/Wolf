package wolf.model;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import wolf.action.Action;
import wolf.bot.IBot;

public abstract class Stage {

  private static final Set<String> admins = ImmutableSet.of("satnam", "semisober");

  private final IBot bot;

  public Stage(IBot bot) {
    this.bot = bot;
  }

  public void handle(IBot bot, String sender, String command, List<String> args, boolean isPrivate) {
    Action action = getActionForCommand(command);

    if (action == null) {
      String message = "Invalid command: " + command;
      if (isPrivate) {
        bot.sendMessage(sender, message);
      } else {
        bot.sendMessage(message);
      }
      action = getActionForCommand("commands");
    }

    if (isPrivate && !action.canBeSentPrivately()) {
      bot.sendMessage(sender, "The " + command + " action does not work as a private message.");
    } else if (!isPrivate && !action.canBeSentPublicly()) {
      bot.sendMessage(sender, "The " + command + " should be sent as a private message.");
    }

    action.apply(new Player(sender, admins.contains(sender)), args, isPrivate);
  }

  private Action getActionForCommand(String command) {
    for (Action a : getAvailableActions()) {
      if (a.getName().equalsIgnoreCase(command)) {
        return a;
      }
    }
    return null;
  }

  public abstract List<Action> getAvailableActions();

  public IBot getBot() {
    return bot;
  }

}
