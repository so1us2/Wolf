package wolf.action.setup;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import wolf.WolfException;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.SetupStage;

public class LoadConfigAction extends SetupAction {

  static final Map<String, Map<Role, Integer>> configs = Maps.newLinkedHashMap();

  static {
    configs.put("Default",
        ImmutableMap.of(Role.SEER, 1, Role.MEDIC, 1, Role.VILLAGER, 5, Role.WOLF, 2));
  }

  public LoadConfigAction(SetupStage stage) {
    super(stage, "load", "configName");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    String configName = args.get(0);
    if (!configs.containsKey(configName)) {
      throw new WolfException(configName + " is an invalid configuration.");
    }
    this.getStage().setAllRoles(configs.get(configName));
  }

  @Override
  public String getDescription() {
    return "Load a preset configuration of roles.";
  }

}
