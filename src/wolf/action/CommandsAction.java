package wolf.action;

import java.util.List;

import wolf.model.Player;
import wolf.model.stage.Stage;

public class CommandsAction extends Action {

  public CommandsAction(Stage stage) {
    super(stage, "commands");
  }

  @Override
  public void apply(Player invoker, List<String> args) {
    getBot().sendMessage(invoker.getName(), "Supported Commands:");
    for (Action a : this.getStage().getAvailableActions(invoker)) {
      getBot().sendMessage(invoker.getName(), a.getUsage() + " - " + a.getDescription());
    }
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    // we only need the apply() method in this case.
  }

  @Override
  public String getDescription() {
    return "Prints out a list of all supported commands.";
  }

}
