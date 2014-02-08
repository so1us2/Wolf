package wolf.bot;

import com.google.common.base.Throwables;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

public abstract class WolfBot extends PircBot {

  private static final String channel = "#MtgWolf";
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

  public void muteAll() {
    setMode(channel, "+m");
    for (User user : getUsers(channel)) {
      deVoice(channel, user.getNick());
    }
  }

  public void unmute(String player) {
    voice(channel, player);
  }

  // Needs to be tested using actual IRC.
  // public void unmutePlayers(List<String> players) {
  // StringBuilder output = new StringBuilder();
  //
  // for (String s : players) {
  // output.append("+v ").append(s).append(" ");
  // }
  // output.setLength(output.length() - 1);
  //
  // this.sendRawLine("MODE " + channel + output.toString());
  // }

  public void unmuteAll() {
    setMode(channel, "-m");
  }

  protected abstract void onMessage(String sender, String message);

  protected abstract void onPrivateMessage(String sender, String message);

  protected void personLeft(String leaver) {
    // Subclasses can override
  }

  @Override
  protected final void onMessage(String channel, String sender, String login, String hostname,
      String message) {
    onMessage(sender, message);
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
