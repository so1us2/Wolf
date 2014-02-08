package wolf.model.role;

import wolf.model.Role;

public class Mason extends AbstractRole {

  @Override
  public void onGameStart() {
    getBot().sendMessage(getPlayer().getName(),
        "The masons are: " + getStage().getPlayers(Role.MASON));
  }

  @Override
  public String getDescription() {
    return "The Masons are villagers who know each other at the start of the game.";
  }

}
