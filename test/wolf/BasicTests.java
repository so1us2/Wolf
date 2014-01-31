package wolf;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import wolf.bot.TestBot;
import wolf.model.GameStage;

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

  @Test
  public void startBasicGame() {
    bot.onMessage("Khaladin", "!newgame");

    bot.onMessage("Khaladin", "!join");
    bot.onMessage("Shallan", "!join");
    bot.onMessage("Dalinar", "!join");
    bot.onMessage("Adolin", "!join");
    bot.onMessage("Navani", "!join");

    bot.onMessage("Khaladin", "!set Villager 3");
    bot.onMessage("Khaladin", "!set Wolf 2");

    bot.onMessage("Khaladin", "!start");

    Assert.assertTrue(bot.getStage() instanceof GameStage);
  }

}
