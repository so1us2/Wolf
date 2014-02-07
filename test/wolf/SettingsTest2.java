package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;
import wolf.model.Role;
import wolf.model.role.Vigilante;

public class SettingsTest2 extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void settingsTest() {
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

    bot.msg("Tom", "!settings");
    bot.msg("Tom", "!setflag REVEAL_NIGHT_KILLERS yes");
    bot.msg("Tom", "!setflag TELL_WOLVES_ON_KILL ROLE");

    bot.msg("Khaladin", "!setrole Villager 2");
    bot.msg("Khaladin", "!setrole Wolf 1");
    bot.msg("Khaladin", "!setrole Seer 1");
    bot.msg("Khaladin", "!setrole Priest 1");
    bot.msg("Khaladin", "!setrole Vigilante 1");
    bot.msg("Khaladin", "!setrole Bartender 1");

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
    bot.privMsg("Jason", "!protect Ian");
    bot.privMsg("Tom", "!peek Jason");
    bot.privMsg("Potter", "!kill Tom");
    bot.privMsg("Ian", "!pass");
    bot.privMsg("Mongo", "!drink Jason");

    checkForMessage("Jason is a villager.");
    checkForMessage("Your wish to protect Ian has been received.");
    checkForMessage(Vigilante.HOLD_FIRE_MESSAGE);
    checkForMessage("You plan to make a drink for Jason.");
    checkForMessage("Jason has a drink waiting for them.");
    checkForMessage("Tom was a Seer.");
    checkForMessage("You find that Tom has been ripped apart.");

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Ian, Jason, Khaladin, Mongo, Potter]");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Khaladin");
    bot.privMsg("Ian", "!vote Potter");
    bot.privMsg("Mongo", "!vote Khaladin");
  }

  private void night2Actions() {
    bot.privMsg("Jason", "!protect Potter");
    bot.privMsg("Potter", "!kill Jason");
    bot.privMsg("Ian", "!shoot Jason");
    bot.privMsg("Mongo", "!drink Ian");

    checkForMessage("Your wish to protect Potter has been received.");
    checkForMessage("You aim at Jason.");
    checkForMessage("You plan to make a drink for Ian.");
    checkForMessage("Ian has a drink waiting for them.");
    checkForMessage("You find that Jason has a single bullet wound in the forehead and has been ripped apart.");
    checkForMessage("Jason was a Priest.");
    bot.getMessageLog().clear();
  }

  private void day3Votes() {
    bot.msg("Ian", "!players");
    checkForMessage("Alive players: [Ian, Mongo, Potter]");

    bot.privMsg("Potter", "!vote Ian");
    bot.privMsg("Ian", "!vote Potter");
    bot.privMsg("Mongo", "!vote Potter");

    checkForMessage("The Villagers have won the game!");
  }

}
