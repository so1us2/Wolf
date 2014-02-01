package wolf.action.setup;

import java.util.List;

import wolf.model.stage.GameStage;
import wolf.model.stage.SetupStage;

import wolf.WolfException;
import wolf.model.Player;

public class StartGameAction extends SetupAction {

  public StartGameAction(SetupStage stage) {
    super(stage, "start");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    if (getStage().getPlayersNeeded() == 0) {
      throw new WolfException("No game configuration loaded.");
    } else if (getStage().getPlayersNeeded() != getStage().getPlayers().size()) {
      throw new WolfException("Number of players does not match number of roles.");
    } else {
      startGame();
    }
  }

  private void startGame() {
    getBot().sendMessage("The game has begun.");

    getBot().setStage(new GameStage(getBot(), getStage().getConfig(), getStage().getPlayers()));
  }

  @Override
  public String getDescription() {
    return "Starts the game.";
  }



}
