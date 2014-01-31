package wolf.action.setup;

import java.util.List;

import wolf.model.GameStage;
import wolf.model.Player;
import wolf.model.SetupStage;

public class StartGameAction extends SetupAction {

  public StartGameAction(SetupStage stage) {
    super(stage, "start", 0);
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    if (getStage().getPlayersNeeded() == 0) {
      getBot().sendMessage("No game configuration loaded.");
    } else if (getStage().getPlayersNeeded() != getStage().getPlayers().size()) {
      getBot().sendMessage("Number of players does not match number of roles.");
    } else {
      startGame();
    }
  }

  private void startGame() {
    getBot().setStage(new GameStage(getBot()));
  }

  @Override
  public String getDescription() {
    return "Starts the game.";
  }



}
