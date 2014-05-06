package wolf.model.configs;

import wolf.model.GameConfig;
import wolf.model.Role;
import wolf.model.Setting;
import wolf.model.Settings;

public class NinePlayerConfig extends GameConfig {

  public NinePlayerConfig() {
    super();
    this.setRole(Role.VILLAGER, 6);
    this.setRole(Role.SEER, 1);
    this.setRole(Role.WOLF, 2);

    for (Setting setting : Settings.getSettingsByCategory().values()) {
      settings.put(setting.getName(), setting.getDefault());
    }
  }

}
