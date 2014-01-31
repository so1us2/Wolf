package wolf.model;

import java.util.List;

import wolf.action.Action;
import wolf.bot.NarratorBot;

public abstract class Stage {

  private final NarratorBot bot;

  public Stage(NarratorBot bot) {
    this.bot = bot;
  }

  public void handle(NarratorBot bot, String sender, String command, List<String> args, boolean isPrivate) {
    Action action = getActionForCommand(command);

    if (action == null) {
      displayHelpText();
    } else {
      if (action.isPrivateAction() != isPrivate) {
        if (isPrivate) {
          bot.sendMessage(sender, "The " + command + " action does not work as a private message.");
        } else {
          bot.sendMessage(sender, "The " + command + " should be sent as a private message.");
        }
      }
    }
  }

  private void displayHelpText() {
    // TODO
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

  public NarratorBot getBot() {
    return bot;
  }

}
