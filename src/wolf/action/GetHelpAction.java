package wolf.action;

import java.util.List;

import wolf.model.Player;
import wolf.model.stage.Stage;

public class GetHelpAction extends Action {

  public GetHelpAction(Stage stage) {
    super(stage, "help");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage(invoker.getName(), "Supported Commands:");
    for (Action a : this.getStage().getAvailableActions(invoker)) {
      getBot().sendMessage(invoker.getName(), a.getUsage() + " - " + a.getDescription());
    }
  }

  @Override
  public String getDescription() {
    return "Prints out a list of all supported commands.";
  }

  @Override
  protected boolean onlyIfAlive() {
    return false;
  }

}
