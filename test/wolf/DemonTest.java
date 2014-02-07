package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import wolf.bot.TestBot;
import wolf.model.Role;
import wolf.model.role.Demon;

/**
 * Tests to see if Bartender can serve drinks and if Vigilante shot is protectable. Also checks if Vigilante can pass properly.
 */
public class DemonTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void demonTest() {
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
    bot.msg("Ian", "!join");
    bot.msg("Mongo", "!join");

    bot.msg("Khaladin", "!setrole Villager 2");
    bot.msg("Khaladin", "!setrole Wolf 1");
    bot.msg("Khaladin", "!setrole Seer 1");
    bot.msg("Khaladin", "!setrole Priest 1");
    bot.msg("Khaladin", "!setrole Demon 1");
    bot.msg("Khaladin", "!setrole Bartender 1");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape", "Khaladin");
    setRole(Role.WOLF, "Potter");
    setRole(Role.SEER, "Tom");
    setRole(Role.PRIEST, "Jason");
    setRole(Role.DEMON, "Ian");
    setRole(Role.BARTENDER, "Mongo");
  }

  private void day1Votes() {
    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    bot.privMsg("Tom", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Snape");
    bot.privMsg("Mongo", "!vote Snape");
    bot.privMsg("Ian", "!vote Potter");
  }

  private void night1Actions() {
    bot.privMsg("Jason", "!protect Tom");
    bot.privMsg("Tom", "!peek Ian");
    bot.privMsg("Potter", "!kill Tom");
    bot.privMsg("Ian", "!pass");
    bot.privMsg("Mongo", "!drink Ian");

    checkForMessage("Ian is a demon.");
    checkForMessage("Your wish to protect Tom has been received.");
    checkForMessage(Demon.NO_KILL_MESSAGE);
    checkForMessage("You plan to make a drink for Ian.");
    checkForMessage("Ian has a drink waiting for them.");
    checkForMessage("You find that Mongo and Tom are dead.");

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Ian, Jason, Khaladin, Potter]");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Khaladin");
    bot.privMsg("Ian", "!vote Khaladin");
  }

  private void night2Actions() {
    bot.privMsg("Jason", "!protect Potter");
    bot.privMsg("Potter", "!kill Ian");
    bot.privMsg("Ian", "!kill Jason");

    checkForMessage("Your wish to protect Potter has been received.");
    checkForMessage("You plan to kill Jason.");
    checkForMessage("You find that Jason and Potter are dead.");
    checkForMessage("The Demons have won the game!");
    bot.getMessageLog().clear();
  }

}
