package wolf.action.game.host;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.InitialStage;
import wolf.model.stage.Stage;

public class AbortGameAction extends Action {

  public AbortGameAction(Stage stage) {
    super(stage, "abort");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().getStage().onAbort();
    getBot().sendMessage("Game aborted by " + invoker.getName());
    getBot().setStage(new InitialStage(getBot()));
    getBot().onPlayersChanged();
  }

  @Override
  public String getDescription() {
    return "Ends a game in progress.";
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

}
