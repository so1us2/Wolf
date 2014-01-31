package wolf.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import wolf.WolfException;
import wolf.bot.WolfBot;

public class GameModel {

  private final WolfBot bot;
  private Stage stage = new InitialStage();

  public GameModel(WolfBot bot) {
    this.bot = bot;
  }

  public void handle(String sender, String command, List<String> args, boolean isPrivate) {
    try {
      stage.handle(this, sender, command, args, isPrivate);
    } catch (WolfException e) {
      bot.sendMessage(e.getMessage());
    }
  }
  
  public void setStage(Stage stage) {
    checkNotNull(stage);

    this.stage = stage;
  }

  public WolfBot getBot() {
    return bot;
  }

}
