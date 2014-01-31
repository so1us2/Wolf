package wolf.model;

import java.util.Map;

import com.google.common.collect.Maps;

public class GameConfig {

  private Map<Role, Integer> roles;

  public GameConfig() {
    roles = Maps.newLinkedHashMap();
  }

  public Map<Role, Integer> getRoles() {
    return roles;
  }

  public void setRole(Role role, int n) {
    roles.put(role, n);
  }

  public void setRoles(Map<Role, Integer> newRoles) {
    roles.clear();
    roles.putAll(newRoles);
  }

}
