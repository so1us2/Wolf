package wolf.action.init;

import java.util.List;

import wolf.WolfBot;

public class NullGameAction extends AbstractInitAction {

  @Override
  public String getCommandName() {
    return "nullgame";
  }

  @Override
  protected void execute(WolfBot bot, String sender, String command, List<String> args) {
    bot.sendMessage("Game has been cancelled!");
    bot.transition(null);
  }

}
