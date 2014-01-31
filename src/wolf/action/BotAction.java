package wolf.action;

import java.util.List;

import wolf.WolfBot;
import wolf.WolfException;

public abstract class BotAction {

  private final int minArguments, maxArguments;

  public BotAction() {
    this(0);
  }

  public BotAction(int numArguments) {
    this.minArguments = numArguments;
    this.maxArguments = numArguments;
  }

  public boolean matches(String command) {
    return this.getCommandName().toLowerCase().startsWith(command.toLowerCase());
  }

  public abstract String getCommandName();

  public void tryInvoke(WolfBot bot, String sender, String command, List<String> args) {
    if (!hasPermission(sender)) {
      throw new WolfException("Permission denied.");
    }

    if (args.size() < minArguments || args.size() > maxArguments) {
      // throw new WolfException(getArgumentsError());
      throw new WolfException(getHelperText());
    }

    execute(bot, sender, command, args);
  }

  public String getHelperText() {
    return getArgumentsError();
  }

  protected String getArgumentsError() {
    if (minArguments != maxArguments) {
      return "Invalid arguments (need between " + minArguments + " and " + maxArguments
          + " arguments)";
    }

    if (minArguments == 0) {
      return "This command shouldn't have any arguments.";
    } else if (minArguments == 1) {
      return "This command should only have one argument.";
    } else {
      return "This command should have " + minArguments + " arguments.";
    }
  }

  protected abstract void execute(WolfBot bot, String sender, String command, List<String> args);

  public boolean hasPermission(String sender) {
    if (requiresAdmin()) {
      return WolfBot.admins.contains(sender.toLowerCase());
    }
    return true;
  }

  protected boolean requiresAdmin() {
    return false;
  }

  public int getMinArguments() {
    return minArguments;
  }

  public int getMaxArguments() {
    return maxArguments;
  }

}
