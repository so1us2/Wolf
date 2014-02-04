package wolf.model.role;

import wolf.model.Faction;
import wolf.model.Role;

public class Minion extends AbstractRole {

  @Override
  public void onGameStart() {
    getBot().sendMessage(getPlayer().getName(),
        "The wolves are: " + getStage().getPlayers(Role.WOLF));
  }

  public Faction getVictoryTeamFaction() {
    return Faction.WOLVES;
  }

  @Override
  public String getDescription() {
    return "The Minion has no special powers but knows who the wolves are and wins with the wolves.";
  }

}
