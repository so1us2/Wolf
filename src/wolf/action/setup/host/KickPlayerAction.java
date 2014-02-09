package wolf.action.setup.host;

import java.util.List;

import wolf.action.setup.SetupAction;
import wolf.model.Player;
import wolf.model.stage.SetupStage;

public class KickPlayerAction extends SetupAction {

  public KickPlayerAction(SetupStage stage) {
    super(stage, "kick", "player");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Player kicked = getStage().getPlayer(args.get(0));
    boolean removed = getStage().removePlayer(kicked);
    if (!removed) {
      getStage().getBot().sendMessage(invoker.getName(), kicked.getName() + " is not in the game.");
    } else {
      getStage().getBot().sendMessage(invoker.getName(), kicked.getName() + " has been kicked.");
    }
  }

  @Override
  public String getDescription() {
    return "Kick a player from the game.";
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

}
