package wolf.model;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import static com.google.common.base.Preconditions.checkNotNull;

public class Settings {

  private static final List<Setting> settings = Lists.newArrayList();

  static {
    category("PRIEST").name("SELF_PROTECT").options("YES", "NO")
        .description("Is the priest allowed to protect themselves?");
    category("PRIEST").name("PROTECTION_MODE").options("EVERY_OTHER_NIGHT", "ONCE_PER_GAME",
        "NO_RULES");
    category("SEER").name("PRE_GAME_PEEK").options("YES", "NO")
        .description("Does the seer get to peek a random villager at the start of the game?");
    category("SEER").name("PRE_GAME_PEEK_MODE").options("REGULAR_VILLAGERS", "ALL_VILLAGERS")
        .description("Can the pre-game peek randomly choose a special-role villager?");
  }

  public static final Setting get(String name) {
    checkNotNull(name);

    for (Setting s : settings) {
      if (name.equalsIgnoreCase(s.getName())) {
        return s;
      }
    }
    throw new IllegalArgumentException("No setting found: " + name);
  }

  public static final Multimap<String, Setting> getSettingsByCategory() {
    LinkedListMultimap<String, Setting> ret = LinkedListMultimap.create();
    for (Setting s : settings) {
      ret.put(s.getCategory(), s);
    }
    return ret;
  }

  private static Setting category(String category) {
    Setting ret = Setting.category(category);
    settings.add(ret);
    return ret;
  }

}
