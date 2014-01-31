package wolf.bot;

import org.jibble.pircbot.PircBot;

import com.google.common.base.Throwables;

public abstract class WolfBot extends PircBot {

  private static final String channel = "#WolfTest";
  private static final String IRC_SERVER = "irc.colosolutions.net";

  public WolfBot(String name) {
    setVerbose(true);

    setName(name);
    setLogin(name);

    startIdentServer();

    try {
      connect(IRC_SERVER);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }

    joinChannel(channel);
  }
  
  public void sendMessage(String message) {
    sendMessage(channel, message);
  }

  protected abstract void onMessage(String sender, String message);

  protected abstract void onPrivateMessage(String sender, String message);

  protected void personLeft(String leaver) {
    // Subclasses can override
  }

  @Override
  protected final void onMessage(String channel, String sender, String login, String hostname, String message) {
    onMessage(sender,message);
  }

  @Override
  protected final void onPrivateMessage(String sender, String login, String hostname, String message) {
    onPrivateMessage(sender, message);
  }

  @Override
  protected final void onPart(String channel, String sender, String login, String hostname) {
    personLeft(sender);
  }

}
