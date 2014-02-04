package wolf.model;

import java.util.Map;

import com.google.common.collect.Maps;

public class GameConfig {

  private final Map<Role, Integer> roles;
  private final Map<String, String> settings;

  public GameConfig() {
    roles = Maps.newLinkedHashMap();
    settings = Maps.newLinkedHashMap();

    for (Setting setting : Settings.getSettingsByCategory().values()) {
      settings.put(setting.getName(), setting.getDefault());
    }
  }

  public Map<Role, Integer> getRoles() {
    return roles;
  }

  public void setRole(Role role, int n) {
    if (n == 0) {
      roles.remove(role);
    } else {
      roles.put(role, n);
    }
  }

  public void setRoles(Map<Role, Integer> newRoles) {
    roles.clear();
    roles.putAll(newRoles);
  }

  public int getPlayersNeeded() {
    int n = 0;
    for (Integer i : roles.values()) {
      n += i;
    }
    return n;
  }

  public Map<String, String> getSettings() {
    return settings;
  }

}
