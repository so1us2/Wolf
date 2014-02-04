package wolf.action.moderator;

import java.util.List;

import wolf.action.Visibility;
import wolf.action.game.GameAction;
import wolf.model.Player;
import wolf.model.stage.GameStage;
import wolf.model.stage.InitialStage;

public class AbortGameAction extends GameAction {

  public AbortGameAction(GameStage stage) {
    super(stage, "abort");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage("Game aborted by " + invoker.getName());
    getBot().setStage(new InitialStage(getBot()));
  }

  @Override
  public String getDescription() {
    return "Ends a game in progress.";
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

  @Override
  public Visibility getVisibility() {
    return Visibility.BOTH;
  }
}
