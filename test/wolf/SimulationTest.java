package wolf;

import wolf.bot.TestBot;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.role.AbstractRole;
import wolf.model.stage.GameStage;

public abstract class SimulationTest {

  protected TestBot bot;

  public SimulationTest setRole(Role role, String... players) {
    GameStage stage = (GameStage) bot.getStage();

    for (String s : players) {
      Player player = stage.getPlayer(s);
      player.setRole(AbstractRole.create(role, player));
      player.getRole().setStage(stage);
    }

    return this;
  }

  public SimulationTest setAdmin(String... players) {
    GameStage stage = (GameStage) bot.getStage();

    for (String s : players) {
      Player player = stage.getPlayer(s);

    }

    return this;
  }

  protected void checkForAbsence(String s) {
    if (bot.getMessageLog().toString().contains(s)) {
      throw new RuntimeException("Found message that shouldn't exist: " + s);
    }
  }

  protected void checkForMessage(String s) {
    if (!bot.getMessageLog().toString().contains(s)) {
      throw new RuntimeException("Could not find message in the log: " + s);
    }
  }

}
