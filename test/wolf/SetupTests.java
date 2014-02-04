package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;

public class SetupTests {

  private TestBot bot;

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void setupTest() {
    initGame();
  }

  public void initGame() {
    bot.msg("Tom", "!newgame");
    bot.msg("Tom", "!allroles");
    bot.msg("Tom", "!details fubar");
    bot.msg("Tom", "!details wolf");
    bot.msg("Tom", "!commands");
  }

}
