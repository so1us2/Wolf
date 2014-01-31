package wolf.action.setup;

import wolf.action.Action;
import wolf.model.GameSetupStage;

public abstract class SetupAction extends Action {

  private final GameSetupStage stage;

  public SetupAction(GameSetupStage stage, String name, int numArgs) {
    super(name, numArgs);

    this.stage = stage;
  }

  public GameSetupStage getStage() {
    return stage;
  }

}
