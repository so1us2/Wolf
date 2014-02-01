package wolf.bot;

import java.util.List;

import wolf.WolfException;
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
  protected void onMessage(String sender, String message) {
    onMessage(sender, message, false);
  }

  @Override
  protected void onPrivateMessage(String sender, String message) {
    onMessage(sender, message, true);
  }

  @Override
  public void onMessage(String sender, String message, boolean isPrivate) {
    handle(this, sender, message, isPrivate);
  }

  @VisibleForTesting
  public static void handle(IBot bot, String sender, String message, boolean isPrivate) {
    if (!message.startsWith("!")) {
      bot.getStage().handleChat(bot, sender, message, isPrivate);
      return;
    }

    List<String> m = ImmutableList.copyOf(Splitter.on(" ").split(message));

    String command = m.get(0).substring(1);
    List<String> args = m.subList(1, m.size());

    try {
      bot.getStage().handle(bot, sender, command, args, isPrivate);
    } catch (WolfException e) {
      if (isPrivate) {
        bot.sendMessage(sender, e.getMessage());
      } else {
        bot.sendMessage(e.getMessage());
      }
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

}
