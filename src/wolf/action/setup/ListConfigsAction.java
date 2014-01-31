package wolf.action.setup;

import java.util.List;
import java.util.Map.Entry;

import wolf.model.GameModel;
import wolf.model.GameSetupStage;
import wolf.model.Player;
import wolf.model.Role;

public class ListConfigsAction extends SetupAction {

  public ListConfigsAction(GameSetupStage stage) {
    super(stage, "configs", 0);
  }

  @Override
  protected void execute(GameModel model, Player invoker, List<String> args) {
    for (String s : LoadConfigAction.configs.keySet()) {
      StringBuilder output = new StringBuilder();
      output.append(s).append(": ");
      for (Entry<Role, Integer> e : LoadConfigAction.configs.get(s).entrySet()) {
        output.append(e.getKey()).append(" (").append(e.getValue()).append("), ");
      }
      output.setLength(output.length() - 2);
      model.getBot().sendMessage(output.toString());
    }
  }

  @Override
  public String getDescription() {
    return "List all pre-set configurations.";
  }

}
