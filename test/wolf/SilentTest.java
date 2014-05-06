package wolf;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import wolf.bot.TestBot;
import wolf.model.Role;

public class SilentTest extends SimulationTest {

  @BeforeMethod
  public void before() {
    bot = new TestBot();
  }

  @Test
  public void silentTest() {
    initGame();
    day1Votes();
    night1Actions();
    day2Votes();
  }

  private void initGame() {
    bot.msg("Khaladin", "!newgame");

    bot.msg("Khaladin", "!join");
    bot.msg("Jason", "!join");
    bot.msg("Tom", "!join");
    bot.msg("Snape", "!join");
    bot.msg("Potter", "!join");

    bot.msg("Khaladin", "!load fives");
    bot.msg("Khaladin", "!setflag SILENT_GAME YES");
    bot.msg("Khaladin", "!setflag VOTING_METHOD END_ON_MAJORITY");
    bot.msg("Khaladin", "!setflag ANNOUNCE_VOTES YES");
    bot.msg("Khaladin", "!setflag WITHDRAW_VOTES YES");
    bot.msg("Khaladin", "!configs");

    bot.msg("Khaladin", "setflag PRE_GAME_PEEK_MODE all_villagers");
    bot.msg("Khaladin", "setflag FIRST_PEEK_MINION yes");

    bot.msg("Khaladin", "!start");

    // we're going to explicitly set the roles so that we can test correctly
    setRole(Role.VILLAGER, "Snape");
    setRole(Role.WOLF, "Potter");
    setRole(Role.SEER, "Tom");
    setRole(Role.HUNTER, "Jason");
    setRole(Role.MINION, "Khaladin");
  }

  private void day1Votes() {
    bot.privMsg("Khaladin", "!vote Snape");
    bot.privMsg("Jason", "!vote Snape");
    checkForMessage("Khaladin-->Snape, Jason-->Snape");
    bot.privMsg("Jason", "!clear");
    checkForMessage("Khaladin-->Snape");
    bot.privMsg("Tom", "!vote Snape");
    bot.privMsg("Snape", "!vote Potter");
    bot.privMsg("Potter", "!vote Snape");
  }

  private void night1Actions() {
    bot.privMsg("Tom", "!peek Jason");
    bot.privMsg("Potter", "!kill Tom");

    checkForMessage("Jason is a villager.");

    bot.getMessageLog().clear();
  }

  private void day2Votes() {
    bot.msg("Jason", "!players");
    checkForMessage("Alive players: [Jason, Khaladin, Potter]");

    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Jason", "!vote Khaladin");
    bot.privMsg("Khaladin", "!clear");
    bot.privMsg("Khaladin", "!vote Potter");
    bot.privMsg("Potter", "!vote Khaladin");
    checkForMessage("The Villagers have won the game!");
  }

}
