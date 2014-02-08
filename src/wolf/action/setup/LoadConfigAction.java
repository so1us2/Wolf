package wolf.action.setup;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.SetupStage;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class LoadConfigAction extends SetupAction {

  static final Map<String, Map<Role, Integer>> configs = Maps.newLinkedHashMap();

  static {
    configs.put("default",
        ImmutableMap.of(Role.SEER, 1, Role.PRIEST, 1, Role.VILLAGER, 5, Role.WOLF, 2));
    configs.put("fives", ImmutableMap.of(Role.SEER, 1, Role.VILLAGER, 1, Role.WOLF, 1, Role.MINION,
        1, Role.HUNTER, 1));
  }

  public LoadConfigAction(SetupStage stage) {
    super(stage, "load", "configName");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    String configName = args.get(0).toLowerCase();
    if (!configs.containsKey(configName)) {
      throw new WolfException(configName + " is an invalid configuration.");
    }
    this.getStage().getConfig().setRoles(configs.get(configName));
    StringBuilder output = new StringBuilder();
    output.append(configName + " loaded: ");
    Map<Role, Integer> roles = getStage().getConfig().getRoles();
    for (Entry<Role, Integer> e : roles.entrySet()) {
      output.append(e.getKey()).append(" (").append(e.getValue()).append("), ");
    }
    output.setLength(output.length() - 2);
    getBot().sendMessage(output.toString());
  }

  @Override
  public String getDescription() {
    return "Load a preset configuration of roles.";
  }

}
