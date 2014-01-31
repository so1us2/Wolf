package wolf.action.setup;

import java.util.List;

import wolf.action.Action;
import wolf.model.GameModel;
import wolf.model.GameSetupStage;
import wolf.model.Player;

public class InitGameAction extends Action {

  public InitGameAction() {
    super("newgame", 0);
  }

  @Override
  protected void execute(GameModel model, Player invoker, List<String> args) {
    model.setStage(new GameSetupStage());
  }

}
