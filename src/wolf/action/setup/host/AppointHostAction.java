package wolf.action.setup.host;

import java.util.List;

import wolf.action.setup.SetupAction;
import wolf.model.Player;
import wolf.model.stage.SetupStage;

public class AppointHostAction extends SetupAction {

  public AppointHostAction(SetupStage stage) {
    super(stage, "appoint", "name");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getStage().setHost(getStage().getPlayer(args.get(0)));
    getStage().getBot().sendMessage(invoker.getName(),
        "You have appointed " + getStage().getHost().getName() + ".");
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

  @Override
  public String getDescription() {
    return "Appoint a new host for the game";
  }

}
