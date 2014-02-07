package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;
import wolf.model.Role;

public class HunterTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void hunterTest() {
    initGame();
    day1Votes();
    night1Actions();
    day2Votes();
  }

  private void initGame() {
    bot.msg("Khaladin", "!newgame");

    bot.msg("Khaladin", "!join");
    bot.msg("Jason", "!join");
    bot.msg("Tom", "!join");
    bot.msg("Snape", "!join");
    bot.msg("Potter", "!join");

    bot.msg("Khaladin", "!setrole Hunter 1");
    bot.msg("Khaladin", "!setrole Wolf 1");
    bot.msg("Khaladin", "!setrole Seer 1");
    bot.msg("Khaladin", "!setrole Villager 1");
    bot.msg("Khaladin", "!setrole Minion 1");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape");
    setRole(Role.WOLF, "Potter");
    setRole(Role.SEER, "Tom");
    setRole(Role.MINION, "Jason");
    setRole(Role.HUNTER, "Khaladin");
  }

  private void day1Votes() {
    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    bot.privMsg("Tom", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Snape");
  }

  private void night1Actions() {
    bot.privMsg("Tom", "!peek Jason");
    bot.privMsg("Potter", "!kill Jason");

    checkForMessage("Jason is a villager.");
    checkForMessage("You find that Jason is dead.");

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Tom", "!players");
    checkForMessage("Alive players: [Khaladin, Potter, Tom]");

    bot.privMsg("Khaladin", "!vote Tom");
    bot.privMsg("Tom", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Tom");

    checkForMessage("The Villagers have won the game!");
  }
}
