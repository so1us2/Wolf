package wolf.action.setup;

import java.util.List;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.stage.SetupStage;

public class JoinAction extends SetupAction {

  public JoinAction(SetupStage stage) {
    super(stage, "join");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    boolean added = getStage().addPlayer(invoker);
    if (!added) {
      throw new WolfException(invoker.getName() + " already joined!");
    }
  }

  @Override
  public String getDescription() {
    return "Joins the game.";
  }

}
