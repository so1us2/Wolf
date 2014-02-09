package wolf.action.setup;

import java.util.List;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.stage.SetupStage;

public class LeaveAction extends SetupAction {

  public LeaveAction(SetupStage stage) {
    super(stage, "leave");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    boolean removed = getStage().removePlayer(invoker);
    if (!removed) {
      throw new WolfException("You aren't in the game!");
    }
  }

  @Override
  public String getDescription() {
    return "Leaves the game.";
  }

}
