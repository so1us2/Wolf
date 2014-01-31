package wolf.action;

import java.util.List;

import wolf.WolfBot;

public class ShutdownAction extends BotAction {

  @Override
  public String getCommandName() {
    return "shutdown";
  }

  @Override
  protected void execute(WolfBot bot, String sender, String command, List<String> args) {
    bot.sendMessage("Shutting down.");
    System.exit(0);
  }

  @Override
  protected boolean requiresAdmin() {
    return true;
  }

}
