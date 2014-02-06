package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;
import wolf.model.Role;
import wolf.model.stage.GameStage;

public class CoreRolesTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void coreRolesTest() {
    initGame();
    day1Votes();
    night1Actions();
    day2Votes();
    night2Actions();
  }

  private void initGame() {
    bot.msg("Khaladin", "!newgame");

    bot.msg("Khaladin", "!join");
    bot.msg("Jason", "!join");
    bot.msg("Tom", "!join");
    bot.msg("Snape", "!join");
    bot.msg("Potter", "!join");

    bot.msg("Khaladin", "!set Villager 2");
    bot.msg("Khaladin", "!set Wolf 1");
    bot.msg("Khaladin", "!set Seer 1");
    bot.msg("Khaladin", "!set Priest 1");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape", "Khaladin");
    setRole(Role.WOLF, "Potter");
    setRole(Role.SEER, "Tom");
    setRole(Role.PRIEST, "Jason");
  }

  private void day1Votes() {
    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    bot.privMsg("Tom", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Snape");
  }

  private void night1Actions() {
    bot.privMsg("Jason", "!protect Tom");
    bot.privMsg("Tom", "!peek Jason");
    bot.privMsg("Potter", "!kill Tom");

    checkForMessage("Jason is a villager.");
    checkForMessage("Your wish to protect Tom has been received.");
    checkForMessage(GameStage.NONE_DEAD_MSG);

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Jason, Khaladin, Potter, Tom]");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("Tom", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Khaladin");
  }

  private void night2Actions() {
    bot.privMsg("Jason", "!protect Jason");
    bot.privMsg("Tom", "!peek Potter");
    bot.privMsg("Potter", "!kill Tom");

    checkForMessage("Potter is a wolf.");
    checkForMessage("The sun dawns and you find Tom dead in the village.");
    checkForMessage("The Wolves have won the game!");
  }

}
