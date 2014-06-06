package wolf.model;

import wolf.WolfException;
import static wolf.model.Faction.DEMONS;
import static wolf.model.Faction.VILLAGERS;
import static wolf.model.Faction.WOLVES;

public enum Role {

  VILLAGER(VILLAGERS), WOLF(WOLVES), SEER(VILLAGERS), PRIEST(VILLAGERS), VIGILANTE(VILLAGERS), BARTENDER(VILLAGERS), HUNTER(VILLAGERS),
  MINION(VILLAGERS), DEMON(DEMONS), SUICIDE_VILLAGER(VILLAGERS), MASON(VILLAGERS), CORRUPTER(WOLVES), ALPHAWOLF(WOLVES);

  private final Faction faction;

  private Role(Faction faction) {
    this.faction = faction;
  }

  public Faction getFaction() {
    return faction;
  }

  @Override
  public String toString() {
    String s = name();
    return Character.toUpperCase(s.charAt(0)) + s.substring(1).toLowerCase();
  }

  public static Role parse(String s) {
    try {
      return Role.valueOf(s.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new WolfException("There is no role \'" + s + "\'.");
    }
  }
}
