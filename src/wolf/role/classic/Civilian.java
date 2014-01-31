package wolf.role.classic;

import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.role.GameRole;

@DisplayName(value = "Civilian", plural = "Civilians")
public class Civilian extends GameRole {

  @Override
  public Faction getFaction() {
    return Faction.VILLAGERS;
  }

}
