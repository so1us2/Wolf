package wolf.action.setup.host;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import wolf.WolfException;
import wolf.action.setup.SetupAction;
import wolf.model.ConfigType;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.SetupStage;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class LoadConfigAction extends SetupAction {

  static final Map<String, ConfigType> configs = Maps.newLinkedHashMap();

  static {
    configs.put("nines",
        new ConfigType("nines").role(Role.SEER, 1).role(Role.VILLAGER, 6).role(Role.WOLF, 2));
    configs.put("elevens", new ConfigType("elevens").role(Role.SEER, 1).role(Role.VILLAGER, 6)
        .role(Role.WOLF, 3).role(Role.PRIEST, 1));
    configs.put(
        "fives",
        new ConfigType("fives").role(Role.SEER, 1).role(Role.VILLAGER, 1).role(Role.WOLF, 1)
            .role(Role.MINION, 1).role(Role.HUNTER, 1).setting("VOTING_METHOD", "ALL_VOTES")
            .setting("PRE_GAME_PEEK_MODE", "ALL_VILLAGERS").setting("FIRST_PEEK_MINION", "YES"));
    configs.put(
        "silent_fives",
        new ConfigType("silent_fives").role(Role.SEER, 1).role(Role.VILLAGER, 1).role(Role.WOLF, 1)
            .role(Role.MINION, 1).role(Role.HUNTER, 1).setting("VOTING_METHOD", "ALL_VOTES")
            .setting("PRE_GAME_PEEK_MODE", "ALL_VILLAGERS").setting("FIRST_PEEK_MINION", "YES")
            .setting("VOTING_METHOD", "END_ON_MAJORITY").setting("ANNOUNCE_VOTES", "YES")
            .setting("WITHDRAW_VOTES", "YES").setting("RATED_GAME", "NO")
            .setting("SILENT_GAME", "YES"));
  }

  public LoadConfigAction(SetupStage stage) {
    super(stage, "load", "configName");
  }

  public static Set<ConfigType> getConfigs() {
    return ImmutableSet.copyOf(configs.values());
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    String configName = args.get(0).toLowerCase();
    if (!configs.containsKey(configName)) {
      throw new WolfException(configName + " is an invalid configuration.");
    }
    this.getStage().getConfig().setRoles(configs.get(configName).getRoles());
    this.getStage().getConfig().resetSettings();
    this.getStage().getConfig().applySettings(configs.get(configName).getSettings());
    StringBuilder output = new StringBuilder();
    output.append(configName + " loaded: ");
    Map<Role, Integer> roles = getStage().getConfig().getRoles();
    for (Entry<Role, Integer> e : roles.entrySet()) {
      output.append(e.getKey()).append(" (").append(e.getValue()).append("), ");
    }
    output.setLength(output.length() - 2);
    getBot().sendMessage(output.toString());
    output = new StringBuilder();
    Map<String, String> settings = configs.get(configName).getSettings();
    for (String s : settings.keySet()) {
      output.append(s).append(" = ").append(settings.get(s)).append(", ");
    }
    output.setLength(output.length() - 2);
    getBot().sendMessage(output.toString());
  }

  @Override
  public String getDescription() {
    return "Load a preset configuration of roles.";
  }

}
