package wolf.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

public class Setting {

  private final String category;
  private String name;
  private String description;
  private String defaultSetting = null;
  private List<String> options = ImmutableList.of();

  public Setting(String category) {
    this.category = category;
  }

  public String getDefault() {
    if (defaultSetting == null) {
      return options.get(0);
    }
    return defaultSetting;
  }

  public Setting defaultSetting(String defaultSetting) {
    this.defaultSetting = defaultSetting;
    return this;
  }

  public Setting name(String name) {
    this.name = name;
    return this;
  }

  public Setting description(String description) {
    this.description = description;
    return this;
  }

  public Setting options(String... options) {
    this.options = ImmutableList.copyOf(options);
    return this;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getCategory() {
    return category;
  }

  public List<String> getOptions() {
    return options;
  }

  public static Setting category(String category) {
    return new Setting(category);
  }

}
