package wolf.action.game;

import java.util.List;

import com.google.common.collect.Iterables;
import wolf.WolfBot;

public class VoteCountAction extends AbstractGameAction {

  @Override
  public String getCommandName() {
    return "votes";
  }

  @Override
  protected void execute(WolfBot bot, String sender, String command, List<String> args) {
    bot.sendMessage(WolfBot.channel,
        getEngine().getNumVotes() + " of " + Iterables.size(getEngine().getAlivePlayers())
            + " players have voted.");
  }

}
