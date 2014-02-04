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
    boolean removed = getStage().getPlayers().remove(invoker);
    if (!removed) {
      throw new WolfException(invoker.getName() + " isn't in the game!");
    }
    getBot().sendMessage(
        invoker.getName() + " left the game (" + getStage().getPlayers().size() + " players)");
  }

  @Override
  public String getDescription() {
    return "Leaves the game.";
  }

}
