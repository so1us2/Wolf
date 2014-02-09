package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;
import wolf.model.Role;
import wolf.model.stage.GameStage;

public class AdminTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void adminTest() {
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
    bot.msg("TomM", "!join");
    bot.msg("Snape", "!join");
    bot.msg("Potter", "!join");

    bot.msg("Khaladin", "!setrole Villager 2");
    bot.msg("Khaladin", "!setrole Wolf 1");
    bot.msg("Khaladin", "!setrole Seer 1");
    bot.msg("Khaladin", "!setrole Priest 1");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape", "Khaladin");
    setRole(Role.WOLF, "Potter");
    setRole(Role.SEER, "TomM");
    setRole(Role.PRIEST, "Jason");
  }

  private void day1Votes() {
    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    bot.privMsg("TomM", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("TomM", "!commands");
    bot.privMsg("TomM", "!voters");
    bot.privMsg("Potter", "!vote Snape");
  }

  private void night1Actions() {
    bot.privMsg("Jason", "!protect TomM");

    bot.privMsg("TomM", "!help");
    bot.privMsg("TomM", "!announce I AM TOM");
    checkForMessage("ANNOUNCEMENT - I AM TOM");
    bot.privMsg("TomM", "!remind");
    checkForMessage("Reminder: please take your night action. The game is waiting on you.");

    bot.privMsg("TomM", "!peek Jason");
    bot.privMsg("Potter", "!kill TomM");

    checkForMessage("Jason is a villager.");
    checkForMessage("Your wish to protect TomM has been received.");
    checkForMessage(GameStage.NONE_DEAD_MSG);

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Jason, Khaladin, Potter, TomM]");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("TomM", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Khaladin");
  }

  private void night2Actions() {
    bot.privMsg("Jason", "!protect Jason");
    bot.privMsg("TomM", "!peek Potter");
    bot.privMsg("Potter", "!kill TomM");

    checkForMessage("Potter is a wolf.");
    checkForMessage("You find that TomM is dead.");
    checkForMessage("The Wolves have won the game!");
  }

}
