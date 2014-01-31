package wolf.bot;

import java.util.List;

import wolf.model.GameModel;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;

public class NarratorBot extends WolfBot {

  private GameModel model = new GameModel(this);

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

    model.handle(sender, command, args, isPrivate);
  }

  public static void main(String[] args) {
    new NarratorBot();
  }

}
