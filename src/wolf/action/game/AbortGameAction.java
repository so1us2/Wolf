package wolf.action.game;

import java.util.List;

import wolf.model.GameStage;
import wolf.model.Player;

public class AbortGameAction extends GameAction {

  public AbortGameAction(GameStage stage) {
    super(stage, "abort", 0);
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    if (!invoker.isAdmin()) {
      getBot().sendMessage("Only administrators may abort a game in progress.");
    }
  }

  @Override
  public String getDescription() {
    return "Aborts a game in progress.";
  }

}
