package wolf.model;

import java.util.List;

import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import static com.google.common.base.Preconditions.checkNotNull;

public class Settings {

  private static final List<Setting> settings = Lists.newArrayList();

  static {
    category("PRIEST").name("SELF_PROTECT").options("YES", "NO").defaultSetting("YES")
        .description("Is the priest allowed to protect themselves?");
    category("PRIEST").name("PROTECTION_MODE")
        .options("EVERY_OTHER_NIGHT", "ONCE_PER_GAME", "NO_RULES")
        .defaultSetting("EVERY_OTHER_NIGHT")
        .description("How often can the priest protect a target?");
    category("SEER").name("PRE_GAME_PEEK").options("YES", "NO")
        .description("Does the seer get to peek a random villager at the start of the game?");
    category("SEER").name("PRE_GAME_PEEK_MODE").options("REGULAR_VILLAGERS", "ALL_VILLAGERS")
        .description("Can the pre-game peek randomly choose a special-role villager?");
    category("SEER").name("FIRST_PEEK_MINION").options("YES", "NO").defaultSetting("YES")
        .description("Does the Minion count as a villager when determining n0 peek?");
    category("GAME").name("PRIVATE_CHAT").options("ENABLED", "DISABLED").defaultSetting("DISABLED")
        .description("Enable or disable private chats amongst players during daytime.");
    category("GAME").name("DAY_KILL_ANNOUNCE").options("FACTION", "SILENT", "ROLE")
        .defaultSetting("FACTION")
        .description("What is announced for players who are voted dead by day?");
    category("GAME").name("NIGHT_KILL_ANNOUNCE").options("NONE", "FACTION", "ROLE")
        .defaultSetting("NONE").description("What is revealed about players who die at night?");
    category("GAME").name("SILENT_GAME").options("YES", "NO").defaultSetting("NO")
        .description("If yes, there is no chatting permitted during the game.");
    category("GAME").name("RATED_GAME").options("YES", "NO").defaultSetting("YES")
        .description("Does the game count towards rankings?");
    category("VOTES")
        .name("VOTING_METHOD")
        .options("END_ON_MAJORITY", "ALL_VOTES")
        .defaultSetting("END_ON_MAJORITY")
        .description("Does voting end as soon as 1 player has a majority or must all players vote?");
    category("VOTES").name("ANNOUNCE_VOTES").options("YES", "NO").defaultSetting("NO")
        .description("Announce each vote as it is cast?");
    category("VOTES").name("WITHDRAW_VOTES").options("YES", "NO").defaultSetting("NO")
        .description("Can you withdraw a vote and be voting for no one?");
    category("WOLF").name("TELL_WOLVES_ON_KILL").options("NONE", "FACTION", "ROLE")
        .defaultSetting("NONE")
        .description("Will wolves be told the role of the player that they kill?");
    category("VIGILANTE").name("TELL_VIG_ON_KILL").options("NONE", "FACTION", "ROLE")
        .defaultSetting("FACTION")
        .description("Will wolves be told the role of the player that they kill?");
    category("DEMON").name("TELL_DEMON_ON_KILL").options("NONE", "FACTION", "ROLE")
        .defaultSetting("NONE")
        .description("Will the demon be told the role of the player it kills?");
    category("GAME").name("REVEAL_NIGHT_KILLERS").options("YES", "NO").defaultSetting("NO")
        .description("Will the killers of each person killed at night be revealed?");
    category("VOTES").name("ANNOUNCE_ON_TIE").options("NONE", "TOTALS", "ALL")
        .defaultSetting("NONE").description("What should be announced when a day vote ties?");
    category("CORRUPTER").name("CORRUPTION_MODE")
        .options("EVERY_OTHER_NIGHT", "ONCE_PER_GAME", "NO_RULES")
        .defaultSetting("EVERY_OTHER_NIGHT").description("How often can a target be corrupted?");
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
