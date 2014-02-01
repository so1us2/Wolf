package wolf.model.stage;

import java.util.List;
import java.util.Set;

import wolf.model.Player;

import com.google.common.collect.ImmutableSet;
import wolf.action.Action;
import wolf.action.Visibility;
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

    if (isPrivate && action.getVisibility() == Visibility.PUBLIC) {
      bot.sendMessage(sender, "The " + command + " action does not work as a private message.");
    } else if (!isPrivate && action.getVisibility() == Visibility.PRIVATE) {
      bot.sendMessage(sender, "The " + command + " should be sent as a private message.");
    }

    action.apply(getPlayer(sender), args, isPrivate);
  }

  protected Player getPlayer(String name) {
    return new Player(name, admins.contains(name));
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
