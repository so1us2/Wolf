package wolf.model;

import java.util.Map;

import com.google.common.collect.Maps;

public class GameConfig {

  protected final Map<Role, Integer> roles;
  protected final Map<String, String> settings;
  protected Player host = null;

  public GameConfig() {
    roles = Maps.newLinkedHashMap();
    settings = Maps.newLinkedHashMap();
    resetSettings();
  }

  private void resetSettings() {
    for (Setting setting : Settings.getSettingsByCategory().values()) {
      settings.put(setting.getName(), setting.getDefault());
    }
  }

  public Player getHost() {
    return host;
  }

  public void setHost(Player host) {
    this.host = host;
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

  public void applySettings(Map<String, String> newSettings) {
    resetSettings();
    for (String s : newSettings.keySet()) {
      settings.put(s, newSettings.get(s));
    }
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

  public boolean isRated() {
    return settings.get("RATED_GAME").equals("YES");
  }

}
