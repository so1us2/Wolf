package wolf.model;

import static wolf.model.Faction.VILLAGERS;
import static wolf.model.Faction.WOLVES;

public enum Role {

  VILLAGER(VILLAGERS), WOLF(WOLVES), SEER(VILLAGERS), MEDIC(VILLAGERS);

  private final Faction faction;

  private Role(Faction faction){
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

}
