package wolf.action.setup;

import java.util.List;

import wolf.action.Action;
import wolf.model.GameModel;
import wolf.model.GameSetupStage;
import wolf.model.Player;

public class CommandsAction extends SetupAction {

  public CommandsAction(GameSetupStage stage) {
    super(stage, "commands", 0);
    // TODO Auto-generated constructor stub
  }
  
  @Override
  protected void execute(GameModel model, Player invoker, List<String> args) {
    model.getBot().sendMessage("Sending a list of supported commands to you now, " + invoker.getName());
      for(Action a : this.getStage().getAvailableActions())
      {
        model.getBot().sendMessage(invoker.getName(), "!" + a.getName() + ": " + a.getDescription());
      }
  }

  @Override
  public String getDescription() {
    return "Prints out a list of all supported commands.";
  }

}
