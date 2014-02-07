package wolf;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import wolf.bot.TestBot;
import wolf.model.Role;
import wolf.model.stage.GameStage;

public class BasicGameTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void createGame() {
    bot.msg("Khaladin", "!newgame");

    bot.msg("Khaladin", "!join");
    bot.msg("Shallan", "!join");
    bot.msg("Dalinar", "!join");

    bot.msg("Khaladin", "!set Villager 5");
    bot.msg("Khaladin", "!set Wolf 2");

    bot.msg("Adolin", "!join");
    bot.msg("Navani", "!join");

    // do a little trolling
    bot.msg("Tom", "!join");
    bot.msg("Tom", "Hey guys, I'm actually not going to play.");
    bot.msg("Tom", "!leave");

    bot.msg("Snape", "!join");
    bot.msg("Potter", "!join");

    bot.msg("Khaladin", "!start");

    Assert.assertTrue(bot.getStage() instanceof GameStage);
    GameStage stage = (GameStage) bot.getStage();
    Assert.assertEquals(stage.getPlayers().size(), 7);

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.WOLF, "Khaladin", "Snape");
    setRole(Role.VILLAGER, "Shallan", "Dalinar", "Adolin", "Navani", "Potter");
  }

  @Test
  public void day1Voting() {
    createGame();

    bot.privMsg("Khaladin", "!vote Navani");
    bot.privMsg("Khaladin", "!vote Shallan");
    checkForMessage("A player switched their vote");

    bot.privMsg("Shallan", "!vote Dalinar");
    bot.privMsg("Dalinar", "!vote Dalinar");
    bot.privMsg("Adolin", "!vote Adolin");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Snape");

    bot.msg("Khaladin", "!votes");
    checkForMessage("6 of 7 players have voted");

    bot.privMsg("Navani", "!vote Adolin");

    checkForMessage("No majority was reached.");

    bot.privMsg("Khaladin", "!vote Adolin");
    bot.privMsg("Shallan", "!vote Dalinar");
    bot.privMsg("Dalinar", "!vote Dalinar");
    bot.privMsg("Adolin", "!vote Adolin");
    bot.privMsg("Navani", "!vote Dalinar");
    bot.privMsg("Snape", "!vote Dalinar");
    bot.privMsg("Potter", "!vote Dalinar");

    checkForMessage("Dalinar was killed");
  }

  @Test
  public void night1Voting() {
    day1Voting();

    checkForMessage("Khaladin: Who do you want to kill?");
    checkForMessage("Snape: Who do you want to kill?");

    bot.privMsg("Snape", "Let's kill Potter?");
    bot.privMsg("Khaladin", "Sure!");

    checkForMessage("<WolfChat> Snape: Let's kill Potter?");
    checkForMessage("<WolfChat> Khaladin: Sure!");

    bot.privMsg("Snape", "!kill potter");
    bot.privMsg("Khaladin", "!kill potter");
  }

  @Test
  public void day2Voting() {
    night1Voting();

    bot.getMessageLog().clear();
    
    bot.msg("Khaladin", "Potter died...it was clearly Snape. Snape hates potter.");
    bot.privMsg("Snape", "!vote Khaladin");
    bot.privMsg("Khaladin", "!vote Snape");

    bot.privMsg("Potter", "!vote Snape");
    checkForAbsence("A player voted. (3 total)");

    bot.privMsg("Shallan", "!vote Snape");
    bot.privMsg("Adolin", "!vote Snape");
    bot.privMsg("Navani", "!vote Snape");
  }

  @Test
  public void night2Voting() {
    day2Voting();

    bot.getMessageLog().clear();
    bot.privMsg("Snape", "!kill Khaladin");
    checkForMessage("Snape is dead");

    bot.getMessageLog().clear();
    bot.privMsg("Khaladin", "!kill Snape");
    checkForMessage("Snape is dead");

    bot.privMsg("Khaladin", "!kill Shallan");
  }

  @Test
  public void day3Voting() {
    night2Voting();

    bot.privMsg("Khaladin", "!vote Adolin");
    bot.privMsg("Adolin", "!vote Khaladin");
    bot.privMsg("Navani", "!vote Khaladin");

    checkForMessage("The Villagers have won the game!");
  }

}
