package wolf.bot;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import wolf.WolfException;
import wolf.model.InitialStage;
import wolf.model.Stage;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public class NarratorBot extends WolfBot {

  private Stage stage = new InitialStage(this);

  public NarratorBot() {
    super("Narrator");
  }

  @Override
  protected void onMessage(String sender, String message) {
    onMessage(sender, message, false);
  }

  @Override
  protected void onPrivateMessage(String sender, String message) {
    onMessage(sender, message, true);
  }

  private void onMessage(String sender, String message, boolean isPrivate) {
    if (!message.startsWith("!")) {
      return;
    }

    List<String> m = ImmutableList.copyOf(Splitter.on(" ").split(message));

    String command = m.get(0).substring(1);
    List<String> args = m.subList(1, m.size());

    try {
      stage.handle(this, sender, command, args, isPrivate);
    } catch (WolfException e) {
      if (isPrivate) {
        sendMessage(sender, e.getMessage());
      } else {
        sendMessage(e.getMessage());
      }
    }
  }

  public void setStage(Stage stage) {
    this.stage = checkNotNull(stage);
  }

  public Stage getStage() {
    return stage;
  }

  public static void main(String[] args) {
    new NarratorBot();
  }

}
