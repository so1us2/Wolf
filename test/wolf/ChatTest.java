package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;
import wolf.model.Role;
import wolf.model.stage.GameStage;

public class ChatTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void chatTest() {
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

    bot.msg("Khaladin", "!set Villager 2");
    bot.msg("Khaladin", "!set Wolf 1");
    bot.msg("Khaladin", "!set Seer 1");
    bot.msg("Khaladin", "!set Priest 1");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape", "Khaladin");
    setRole(Role.WOLF, "Potter");
    setRole(Role.SEER, "TomM");
    setRole(Role.PRIEST, "Jason");
  }

  private void day1Votes() {
    bot.privMsg("Khaladin", "!newroom myroom");
    bot.privMsg("Khaladin", "!authorize TomM");
    bot.privMsg("Jason", "!joinroom myroom");
    bot.privMsg("TomM", "!joinroom myroom");
    bot.privMsg("Jason", "!chat hi");
    bot.privMsg("TomM", "!chat hi");
    bot.privMsg("TomM", "!authorize Snape");
    bot.privMsg("Snape", "!joinroom myroom");
    bot.privMsg("Snape", "!chat hi everybody!");
    bot.privMsg("TomM", "!newroom coolkids");
    bot.privMsg("TomM", "!authorize Jason");
    bot.privMsg("Jason", "!joinroom coolkids");
    bot.privMsg("TomM", "!chat hi Jason");
    bot.privMsg("TomM", "!listrooms");
    bot.msg("Snape", "!listrooms");


    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    bot.privMsg("TomM", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Snape");
  }

  private void night1Actions() {
    bot.privMsg("Jason", "!protect TomM");
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
    bot.privMsg("TomM", "!listrooms");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("TomM", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Khaladin");
  }

  private void night2Actions() {
    bot.privMsg("Jason", "!protect Jason");
    bot.privMsg("TomM", "!peek Potter");
    bot.privMsg("Potter", "!kill TomM");

    checkForMessage("RAWRRRR!! Potter is a wolf.");
    checkForMessage("The sun dawns and you find TomM dead in the village.");
    checkForMessage("The Wolves have won the game!");
  }

}
