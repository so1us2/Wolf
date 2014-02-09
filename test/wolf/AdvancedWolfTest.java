package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;
import wolf.model.Role;
import wolf.model.role.Vigilante;
import wolf.model.stage.GameStage;

/**
 * Tests to see if Bartender can serve drinks and if Vigilante shot is protectable. Also checks if Vigilante can pass properly.
 */
public class AdvancedWolfTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void advanceWolfTest() {
    initGame();
    day1Votes();
    night1Actions();
    day2Votes();
    night2Actions();
    day3Votes();
    day3Actions();
    day4Votes();
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
    bot.msg("Mark", "!join");
    bot.msg("Vince", "!join");

    bot.msg("Khaladin", "!setrole Villager 3");
    bot.msg("Khaladin", "!setrole Corrupter 1");
    bot.msg("Khaladin", "!setrole AlphaWolf 1");
    bot.msg("Khaladin", "!setrole Seer 1");
    bot.msg("Khaladin", "!setrole Priest 1");
    bot.msg("Khaladin", "!setrole Vigilante 1");
    bot.msg("Khaladin", "!setrole Bartender 1");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape", "Khaladin", "Vince");
    setRole(Role.CORRUPTER, "Potter");
    setRole(Role.SEER, "Tom");
    setRole(Role.PRIEST, "Jason");
    setRole(Role.VIGILANTE, "Ian");
    setRole(Role.BARTENDER, "Mongo");
    setRole(Role.ALPHAWOLF, "Mark");
  }

  private void day1Votes() {
    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    bot.privMsg("Tom", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Snape");
    bot.privMsg("Mongo", "!vote Snape");
    bot.privMsg("Ian", "!vote Potter");
    bot.privMsg("Mark", "!vote Snape");
    bot.privMsg("Vince", "!vote Snape");
  }

  private void night1Actions() {
    bot.privMsg("Jason", "!protect Tom");
    bot.privMsg("Tom", "!peek Jason");
    bot.privMsg("Potter", "!kill Tom");
    bot.privMsg("Ian", "!shoot Mongo");
    bot.privMsg("Mongo", "!drink Jason");
    bot.privMsg("Potter", "!corrupt Ian");
    bot.privMsg("Mark", "!kill Tom");
    bot.privMsg("Mark", "!sniff Jason");


    checkForMessage("Jason is a villager.");
    checkForMessage("Your wish to protect Tom has been received.");
    checkForMessage("You plan to make a drink for Jason.");
    checkForMessage("Jason has a drink waiting for them.");
    checkForMessage("Jason smells like a priest.");
    checkForMessage(Vigilante.CORRUPTED_MESSAGE);
    checkForMessage(GameStage.NONE_DEAD_MSG);

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Ian, Jason, Khaladin, Mark, Mongo, Potter, Tom, Vince]");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("Tom", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Khaladin");
    bot.privMsg("Ian", "!vote Tom");
    bot.privMsg("Mongo", "!vote Khaladin");
    bot.privMsg("Vince", "!vote Khaladin");
    bot.privMsg("Mark", "!vote Khaladin");
  }

  private void night2Actions() {
    bot.privMsg("Jason", "!protect Mongo");
    bot.privMsg("Tom", "!peek Potter");
    bot.privMsg("Potter", "!kill Mongo");
    bot.privMsg("Ian", "!shoot Jason");
    bot.privMsg("Mongo", "!drink Ian");
    bot.privMsg("Potter", "!corrupt Jason");
    bot.privMsg("Mark", "!kill Mongo");
    bot.privMsg("Mark", "!sniff Ian");

    checkForMessage("Potter is a wolf.");
    checkForMessage("Your wish to protect Mongo has been received.");
    checkForMessage("You aim at Jason.");
    checkForMessage("You plan to make a drink for Ian.");
    checkForMessage("Ian has a drink waiting for them.");
    checkForMessage("You shoot Jason square between the eyes.");
    checkForMessage("Ian smells like a vigilante.");
    checkForMessage("You find that Jason and Mongo are dead.");
    bot.getMessageLog().clear();
  }

  private void day3Votes() {
    bot.msg("Tom", "!players");
    checkForMessage("Alive players: [Ian, Mark, Potter, Tom, Vince]");

    bot.privMsg("Tom", "!vote Mark");
    bot.privMsg("Potter", "!vote Vince");
    bot.privMsg("Ian", "!vote Mark");
    bot.privMsg("Mark", "!vote Vince");
    bot.privMsg("Vince", "!vote Mark");
  }

  private void day3Actions() {
    bot.privMsg("Tom", "!peek Potter");
    bot.privMsg("Potter", "!kill Vince");
    bot.privMsg("Potter", "!corrupt Tom");

    checkForMessage("Potter is a villager.");
    checkForMessage("You find that Vince is dead.");
    bot.getMessageLog().clear();
  }

  private void day4Votes() {
    bot.msg("Tom", "!players");
    checkForMessage("Alive players: [Ian, Potter, Tom]");

    bot.privMsg("Potter", "!vote Ian");
    bot.privMsg("Ian", "!vote Potter");
    bot.privMsg("Tom", "!vote Ian");

    checkForMessage("The Wolves have won the game!");
  }


}
