package wolf.action.setup;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.SetupStage;

public class GameStatusAction extends SetupAction {

  public GameStatusAction(SetupStage stage) {
    super(stage, "status");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    if (getStage().getHost() == null) {
      getBot().sendMessage(invoker.getName(), "There is currently no host.");
    } else {
      getBot().sendMessage(invoker.getName(), "The game host is " + getStage().getHost() + ".");
    }
    if (getStage().getPlayers().isEmpty()) {
      getBot().sendMessage(invoker.getName(), "There are no players in the game.");
    } else {
      getBot().sendMessage(invoker.getName(),
          "There are " + getStage().getPlayers().size() + " players registered.");
    }
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
    return "List the status of the currently forming game.";
  }

}
