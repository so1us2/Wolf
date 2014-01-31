package wolf.action.setup;

import java.util.List;
import java.util.Map;

import wolf.model.GameModel;
import wolf.model.GameSetupStage;
import wolf.model.Player;
import wolf.model.Role;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class LoadConfigAction extends SetupAction {

  static final Map<String, Map<Role, Integer>> configs = Maps.newLinkedHashMap();

  static {
    configs
        .put(
            "Default",
            ImmutableMap.of(Role.SEER, 1, Role.MEDIC, 1, Role.VILLAGER, 5,
                Role.WOLF, 2));
  }

  public LoadConfigAction(GameSetupStage stage) {
    super(stage, "load", 1);
  }

  @Override
  protected void execute(GameModel model, Player invoker, List<String> args) {
    if (!configs.containsKey(args.get(0))) {
      model.getBot().sendMessage(args.get(0) + " is an invalid configuration.");
    }
    this.getStage().replaceRoles(configs.get(args.get(0)));
  }

  @Override
  public String getDescription() {
    return "Load a preset configuration of roles.";
  }


}
