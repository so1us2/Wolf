package wolf.action.moderator;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;

public class AnnounceAction extends Action {

  public AnnounceAction(String name, String[] argsNames) {
    super("announce", "message");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage("ANNOUNCEMENT - " + args.get(0));
  }

  @Override
  public String getDescription() {
    return "Have the bot send an announcement to the channel.";
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

}
