package wolf;

import wolf.model.stage.GameStage;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import wolf.bot.TestBot;

public class BasicGameTest {

  private TestBot bot;

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void basicGame() {
    startGame();

    Assert.assertTrue(bot.getStage() instanceof GameStage);
    GameStage stage = (GameStage) bot.getStage();
    Assert.assertEquals(stage.getPlayers().size(), 5);
  }

  @Test
  public void daytimeVoting() {
    startGame();

    bot.privMsg("Khaladin", "!vote Navani");
    bot.privMsg("Khaladin", "!vote Shallan");
    checkForMessage("A player switched their vote");

    bot.privMsg("Shallan", "!vote Dalinar");
    bot.privMsg("Dalinar", "!vote Dalinar");
    bot.privMsg("Adolin", "!vote Adolin");

    bot.msg("Khaladin", "!votes");
    checkForMessage("4 of 5 players have voted");

    bot.privMsg("Navani", "!vote Adolin");
  }

  private void startGame() {
    bot.msg("Khaladin", "!newgame");

    bot.msg("Khaladin", "!join");
    bot.msg("Shallan", "!join");
    bot.msg("Dalinar", "!join");

    bot.msg("Khaladin", "!set Villager 3");
    bot.msg("Khaladin", "!set Wolf 2");

    bot.msg("Adolin", "!join");
    bot.msg("Navani", "!join");

    // do a little trolling
    bot.msg("Tom", "!join");
    bot.msg("Tom", "Hey guys, I'm actually not going to play.");
    bot.msg("Tom", "!leave");

    bot.msg("Khaladin", "!start");
  }

  private void checkForMessage(String s) {
    if (!bot.getMessageLog().toString().contains(s)) {
      throw new RuntimeException("Could not find message in the log: " + s);
    }
  }

}
