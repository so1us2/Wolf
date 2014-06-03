package wolf.bot;

import java.util.List;

import wolf.ChatLogger;
import wolf.WolfException;
import wolf.model.stage.GameStage;
import wolf.model.stage.InitialStage;
import wolf.model.stage.Stage;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkNotNull;

public class NarratorBot extends WolfBot implements IBot {

  private Stage stage = new InitialStage(this);

  public NarratorBot() {
    super("Moderator");
  }

  @Override
  protected void onPrivateMessage(String sender, String message) {
    onMessage(sender, message);
  }

  @Override
  public void sendToAll(String command, Object... params) {}

  @Override
  public void onMessage(String sender, String message) {
    handle(this, sender, message);
  }

  @Override
  public void sendToAll(String from, String message) {
    sendMessage(from + ": " + message);
  }

  @VisibleForTesting
  public static void handle(IBot bot, String sender, String message) {
    if (!message.startsWith("!")) {
      bot.getStage().handleChat(bot, sender, message);
      return;
    }

    List<String> m = ImmutableList.copyOf(Splitter.on(" ").split(message));

    String command = m.get(0).substring(1);
    List<String> args = m.subList(1, m.size());

    try {
      bot.getStage().handle(bot, sender, command, args);
    } catch (WolfException e) {
      bot.sendMessage(sender, e.getMessage());
    }
  }

  @Override
  public void setStage(Stage stage) {
    this.stage = checkNotNull(stage);
  }

  @Override
  public Stage getStage() {
    return stage;
  }

  public static void main(String[] args) {
    new NarratorBot();
  }

  @Override
  public void onPlayersChanged() {}

  @Override
  public void recordGameResults(GameStage stage) {}

  @Override
  public boolean isAdmin(String user) {
    return false;
  }

  @Override
  public void setLogger(ChatLogger logger) {}

}
