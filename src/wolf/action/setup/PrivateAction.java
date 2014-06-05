package wolf.action.setup;

import java.util.List;

import wolf.model.Player;
import wolf.model.stage.SetupStage;

public class PrivateAction extends SetupAction {

  public PrivateAction(SetupStage stage) {
    super(stage, "private");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getStage().setPrivate(true);
    getBot().sendMessage(invoker.getName() + " has made this a private game. " +
            "You must be invited in order to join.");
  }

  @Override
  public String getDescription() {
    return "Makes the game private so that only invited players may join.";
  }

  @Override
  protected boolean requiresHost() {
    return true;
  }


}
