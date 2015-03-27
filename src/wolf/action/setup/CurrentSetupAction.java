package wolf.action.setup;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import wolf.action.Action;
import wolf.bot.IBot;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.Stage;

public class CurrentSetupAction extends Action {

  public CurrentSetupAction(Stage stage) {
    super(stage, "current");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage(invoker.getName(), printRoles(getStage(), getBot()));
  }

  public static String printRoles(Stage stage, IBot bot) {
    Map<Role, Integer> roles = stage.getConfig().getRoles();

    if (roles.isEmpty()) {
      return "No roles have been added yet.";
    } else {
      StringBuilder output = new StringBuilder();
      output.append("Current roles: ");
      for (Entry<Role, Integer> e : roles.entrySet()) {
        output.append(e.getKey()).append(" (").append(e.getValue()).append("), ");
      }
      output.setLength(output.length() - 2);
      return output.toString();
    }
  }

  @Override
  public String getDescription() {
    return "List roles currently loaded in the game.";
  }

}
