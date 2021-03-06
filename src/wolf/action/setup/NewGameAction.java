package wolf.action.setup;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.InitialStage;
import wolf.model.stage.SetupStage;

public class NewGameAction extends Action {

  private final InitialStage stage;

  public NewGameAction(InitialStage stage) {
    super(stage, "newgame");

    this.stage = stage;
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    boolean downtime = false;

    if (downtime) {
      getBot().sendMessage(invoker.getName(),
          "You can't start a game because server downtime has not completed.");
      return;
    }

    getBot().sendMessage("A new game is forming -- type /join");

    SetupStage setup = new SetupStage(stage.getBot());
    getBot().setStage(setup);

    setup.addPlayer(invoker);
  }

  @Override
  public String getDescription() {
    return "Begins setup for a new game.";
  }

}
