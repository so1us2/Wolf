package wolf.action.setup;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.SetupStage;

public class CurrentSetupAction extends SetupAction {

  public CurrentSetupAction(SetupStage stage) {
    super(stage, "current");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {

    Map<Role, Integer> roles = getStage().getConfig().getRoles();

    if (roles.isEmpty()) {
      getBot().sendMessage(invoker.getName(), "No roles have been added yet.");
    } else {
      StringBuilder output = new StringBuilder();
      output.append("Current roles: ");
      for (Entry<Role, Integer> e : roles.entrySet()) {
        output.append(e.getKey()).append(" (").append(e.getValue()).append("), ");
      }
      output.setLength(output.length() - 2);
      getBot().sendMessage(invoker.getName(), output.toString());
    }
  }

  @Override
  public String getDescription() {
    return "List roles currently loaded in the game.";
  }

}
