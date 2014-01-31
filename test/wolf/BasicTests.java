package wolf;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import wolf.bot.TestBot;

public class BasicTests {

  private TestBot bot;

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void invalidCommand() {
    bot.onMessage("Khaladin", "!hello", true);
    
    Assert.assertTrue(bot.getMessageLog().toString().contains("!newgame"));
  }

}
