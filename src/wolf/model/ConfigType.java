package wolf.model;

import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

public class ConfigType {
  private final Map<Role, Integer> roles = Maps.newHashMap();
  private final Map<String, String> settings = Maps.newHashMap();
  private final String name;

  public ConfigType(String name) {
    this.name = name;
  }

  public ConfigType setting(String setting, String value) {
    settings.put(setting, value);
    return this;
  }

  public ConfigType role(Role role, int num) {
    roles.put(role, num);
    return this;
  }

  public Map<Role, Integer> getRoles() {
    return ImmutableMap.copyOf(roles);
  }

  public Map<String, String> getSettings() {
    return ImmutableMap.copyOf(settings);
  }

  public String getName() {
    return name;
  }
}
