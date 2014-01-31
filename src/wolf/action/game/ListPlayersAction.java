package wolf.action.game;

import java.util.List;

import wolf.WolfBot;

import com.google.common.base.Joiner;

public class ListPlayersAction extends AbstractGameAction {
  @Override
  public String getCommandName() {
    return "players";
  }

  @Override
  protected void execute(WolfBot bot, String sender, String command, List<String> args) {
    bot.sendMessage(WolfBot.channel, Joiner.on(' ').join(engine.getNamePlayerMap().values()));
  }
}
