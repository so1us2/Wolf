package wolf.action.setup;

import wolf.action.Action;
import wolf.model.stage.SetupStage;

public abstract class SetupAction extends Action {

  private final SetupStage stage;

  public SetupAction(SetupStage stage, String name, String... argNames) {
    super(stage, name, argNames);

    this.stage = stage;
  }

  @Override
  public SetupStage getStage() {
    return stage;
  }

}
