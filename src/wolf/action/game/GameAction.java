package wolf.action.game;

import wolf.action.Action;
import wolf.model.GameStage;

public abstract class GameAction extends Action {
 
    private final GameStage stage;

    public GameAction(GameStage stage, String name, int numArgs) {
      super(stage, name, numArgs);

      this.stage = stage;
    }

    public GameStage getStage() {
      return stage;
    }
}
