package wolf.action.setup;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.SetupStage;

public class CommandsAction extends SetupAction {

  public CommandsAction(SetupStage stage) {
    super(stage, "commands");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage("Sending a list of supported commands to you now, " + invoker.getName());
    for (Action a : this.getStage().getAvailableActions()) {
      getBot().sendMessage(invoker.getName(), a.getDescription() + " - " + a.getDescription());
    }
  }

  @Override
  public String getDescription() {
    return "Prints out a list of all supported commands.";
  }

}
