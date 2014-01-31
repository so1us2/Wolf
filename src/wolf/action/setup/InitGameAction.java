package wolf.action.setup;

import java.util.List;

import wolf.action.Action;
import wolf.model.SetupStage;
import wolf.model.InitialStage;
import wolf.model.Player;

public class InitGameAction extends Action {

  private final InitialStage stage;

  public InitGameAction(InitialStage stage) {
    super(stage, "newgame", 0);

    this.stage = stage;
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().setStage(new SetupStage(stage.getBot()));
  }

  @Override
  public String getDescription() {
    return "Begins setup for a new game.";
  }

}
