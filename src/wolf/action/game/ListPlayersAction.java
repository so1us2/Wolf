package wolf.action.game;

import java.util.List;

import com.google.common.base.Joiner;
import wolf.WolfBot;

public class ListPlayersAction extends AbstractGameAction {
  @Override
  public String getCommandName() {
    return "players";
  }

  @Override
  protected void execute(WolfBot bot, String sender, String command, List<String> args) {
    bot.sendMessage(WolfBot.channel, Joiner.on(' ').join(getEngine().getNamePlayerMap().values()));
  }
}
