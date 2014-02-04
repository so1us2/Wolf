package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;
import wolf.model.Role;
import wolf.model.stage.GameStage;

public class CoreRolesTest2 extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void coreRolesTest2() {
    initGame();
    day1Votes();
    night1Actions();
    day2Votes();
    night2Actions();
    day3Votes();
    night3Actions();
  }

  private void initGame() {
    bot.msg("Khaladin", "!newgame");

    bot.msg("Khaladin", "!join");
    bot.msg("Jason", "!join");
    bot.msg("Tom", "!join");
    bot.msg("Snape", "!join");
    bot.msg("Potter", "!join");
    bot.msg("Will", "!join");
    bot.msg("Joe", "!join");
    bot.msg("Bob", "!join");
    bot.msg("Zack", "!join");

    bot.msg("Khaladin", "!load Default");

    checkForMessage("default loaded.");

    bot.msg("Zack", "!roles");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape", "Khaladin", "Joe", "Bob", "Zack");
    setRole(Role.WOLF, "Potter", "Will");
    setRole(Role.SEER, "Tom");
    setRole(Role.PRIEST, "Jason");
    bot.getMessageLog().clear();
  }

  private void day1Votes() {
    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    bot.privMsg("Tom", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Potter");
    bot.privMsg("Will", "!vote Potter");
    bot.privMsg("Joe", "!vote Potter");
    bot.privMsg("Bob", "!vote Joe");
    bot.privMsg("Zack", "!vote Joe");

    checkForMessage("No majority was reached.");

    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    bot.privMsg("Tom", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Potter");
    bot.privMsg("Will", "!vote Potter");
    bot.privMsg("Joe", "!vote Joe");
    bot.privMsg("Joe", "!vote Snape");

    checkForMessage("Switched vote to Snape");
    checkForMessage("A player switched their vote. (7 total)");

    bot.privMsg("Bob", "!vote Snape");
    bot.privMsg("Zack", "!vote Snape");

    checkForMessage("A verdict was reached and Snape was killed.");
    bot.getMessageLog().clear();
  }

  private void night1Actions() {
    bot.privMsg("Jason", "!protect Tom");
    bot.privMsg("Tom", "!peek Jason");
    bot.privMsg("Potter", "!kill Tom");
    bot.privMsg("Will", "!kill Tom");


    checkForMessage("Jason is a villager.");
    checkForMessage("Your wish to protect Tom has been received.");
    checkForMessage(GameStage.NONE_DEAD_MSG);

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Bob, Jason, Joe, Khaladin, Potter, Tom, Will, Zack]");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("Tom", "!vote Khaladin");
    bot.privMsg("Potter", "!vote Khaladin");
    bot.privMsg("Will", "!vote Potter");
    bot.privMsg("Joe", "!vote Khaladin");
    bot.privMsg("Bob", "!vote Khaladin");
    bot.privMsg("Zack", "!vote Joe");

    checkForMessage("A verdict was reached and Khaladin was killed.");
    bot.getMessageLog().clear();
  }

  private void night2Actions() {
    bot.privMsg("Jason", "!protect Jason");
    bot.privMsg("Tom", "!peek Potter");
    bot.privMsg("Potter", "!kill Tom");
    bot.privMsg("Will", "!kill Tom");

    checkForMessage("RAWRRRR!! Potter is a wolf.");
    checkForMessage("The sun dawns and you find Tom dead in the village.");
    bot.getMessageLog().clear();
  }

  private void day3Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Bob, Jason, Joe, Potter, Will, Zack]");

    bot.privMsg("Jason", "!vote Joe");
    bot.privMsg("Potter", "!vote Will");
    bot.privMsg("Will", "!vote Joe");
    bot.privMsg("Joe", "!vote Will");
    bot.privMsg("Bob", "!vote Joe");
    bot.privMsg("Zack", "!vote Joe");

    checkForMessage("A verdict was reached and Joe was killed.");
    bot.getMessageLog().clear();
  }

  private void night3Actions() {
    bot.privMsg("Jason", "!protect Jason");

    checkForMessage("You cannot protect Jason twice in a row.");

    bot.privMsg("Potter", "!kill Jason");
    bot.privMsg("Will", "!kill Jason");
    bot.privMsg("Jason", "!protect Zack");

    checkForMessage("The sun dawns and you find Jason dead in the village.");
    checkForMessage("The Wolves have won the game!");

  }
}
