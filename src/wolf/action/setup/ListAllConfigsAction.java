package wolf.action.setup;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.action.setup.host.LoadConfigAction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.SetupStage;

public class ListAllConfigsAction extends SetupAction {

  public ListAllConfigsAction(SetupStage stage) {
    super(stage, "configs");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Map<String, Map<Role, Integer>> configs = LoadConfigAction.getConfigs();
    for (String s : configs.keySet()) {
      StringBuilder output = new StringBuilder();
      output.append(s).append(": ");
      for (Entry<Role, Integer> e : configs.get(s).entrySet()) {
        output.append(e.getKey()).append(" (").append(e.getValue()).append("), ");
      }
      output.setLength(output.length() - 2);
      getBot().sendMessage(invoker.getName(), output.toString());
    }
  }

  @Override
  public String getDescription() {
    return "List all pre-set configurations.";
  }

}
