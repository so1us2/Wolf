package wolf.bot;

import java.util.Random;

public class MonkeyBot extends WolfBot {

  private static final Random rand = new Random();

  public MonkeyBot() {
    super(generateMonkeyName());
  }

  @Override
  protected void onMessage(String sender, String message) {
  }

  @Override
  protected void onPrivateMessage(String sender, String message) {
  }

  private static final String generateMonkeyName() {
    return "Monkey" + rand.nextInt(99) + 1;
  }

}
