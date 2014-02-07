package wolf.action.setup;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.InitialStage;
import wolf.model.stage.SetupStage;

public class InitGameAction extends Action {

  private final InitialStage stage;

  public InitGameAction(InitialStage stage) {
    super(stage, "newgame");

    this.stage = stage;
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage("A new game is forming -- type /join");

    getBot().setStage(new SetupStage(stage.getBot()));
  }

  @Override
  public String getDescription() {
    return "Begins setup for a new game.";
  }

}
