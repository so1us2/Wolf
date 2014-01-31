package wolf;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import wolf.bot.TestBot;
import wolf.model.GameStage;

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

}
