package wolf.model.role;

import wolf.model.Faction;
import wolf.model.Role;

public class Minion extends AbstractRole {

  @Override
  public void onGameStart() {
    getBot().sendMessage(getPlayer().getName(),
        "The wolves are: " + getStage().getPlayers(Role.WOLF));
  }

  @Override
  public Faction getVisibleFaction() {
    return Faction.VILLAGERS;
  }

}
