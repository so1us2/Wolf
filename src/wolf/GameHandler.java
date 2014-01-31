package wolf;

public interface GameHandler {

  public void onMessage(WolfBot bot, String channel, String sender, String login, String hostname,
      String message);

  public void onPrivateMessage(WolfBot bot, String sender, String login, String hostname,
      String message);

  public void onPart(WolfBot bot, String channel, String sender, String login, String hostname);

}
