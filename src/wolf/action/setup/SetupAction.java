package wolf.action.setup;

import wolf.model.stage.SetupStage;

import wolf.action.Action;

public abstract class SetupAction extends Action {

  private final SetupStage stage;

  public SetupAction(SetupStage stage, String name, String... argNames) {
    super(stage, name, argNames);

    this.stage = stage;
  }

  public SetupStage getStage() {
    return stage;
  }


}
