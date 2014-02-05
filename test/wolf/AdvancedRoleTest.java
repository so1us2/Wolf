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
public class AdvancedRoleTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void advancedRoleTest() {
    initGame();
    day1Votes();
    night1Actions();
    day2Votes();
    night2Actions();
    day3Votes();
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

    bot.msg("Khaladin", "!set Villager 2");
    bot.msg("Khaladin", "!set Wolf 1");
    bot.msg("Khaladin", "!set Seer 1");
    bot.msg("Khaladin", "!set Priest 1");
    bot.msg("Khaladin", "!set Vigilante 1");
    bot.msg("Khaladin", "!set Bartender 1");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape", "Khaladin");
    setRole(Role.WOLF, "Potter");
    setRole(Role.SEER, "Tom");
    setRole(Role.PRIEST, "Jason");
    setRole(Role.VIGILANTE, "Ian");
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
    bot.privMsg("Tom", "!peek Jason");
    bot.privMsg("Potter", "!kill Tom");
    bot.privMsg("Ian", "!pass");
    bot.privMsg("Mongo", "!drink Jason");

    checkForMessage("Jason is a villager.");
    checkForMessage("Your wish to protect Tom has been received.");
    checkForMessage(Vigilante.HOLD_FIRE_MESSAGE);
    checkForMessage("You plan to make a drink for Jason.");
    checkForMessage("Jason has a drink waiting for them.");
    checkForMessage(GameStage.NONE_DEAD_MSG);

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Ian, Jason, Khaladin, Mongo, Potter, Tom]");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("Tom", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Khaladin");
    bot.privMsg("Ian", "!vote Tom");
    bot.privMsg("Mongo", "!vote Khaladin");
  }

  private void night2Actions() {
    bot.privMsg("Jason", "!protect Jason");
    bot.privMsg("Tom", "!peek Potter");
    bot.privMsg("Potter", "!kill Tom");
    bot.privMsg("Ian", "!shoot Jason");
    bot.privMsg("Mongo", "!drink Ian");

    checkForMessage("Potter is a wolf.");
    checkForMessage("Your wish to protect Jason has been received.");
    checkForMessage("You aim at Jason.");
    checkForMessage("You plan to make a drink for Ian.");
    checkForMessage("Ian has a drink waiting for them.");
    checkForMessage("The sun dawns and you find Tom dead in the village.");
    bot.getMessageLog().clear();
  }

  private void day3Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Ian, Jason, Mongo, Potter]");

    bot.privMsg("Jason", "!vote Potter");
    bot.privMsg("Potter", "!vote Jason");
    bot.privMsg("Ian", "!vote Potter");
    bot.privMsg("Mongo", "!vote Potter");
    
    checkForMessage("The Villagers have won the game!");
  }


}
