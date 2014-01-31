package wolf.action.setup;

import java.util.List;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.SetupStage;

public class LeaveAction extends SetupAction {

  public LeaveAction(SetupStage stage) {
    super(stage, "leave", 0);
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    boolean added = getStage().getPlayers().add(invoker);
    if (added) {
      throw new WolfException(invoker.getName() + " not in game!");
    }
    getBot().sendMessage(invoker.getName() + " left the game.");
  }
  
  @Override
  public String getDescription() {
    return "Leaves the game.";
  }

}
