package wolf.action.game;

import java.util.List;

import wolf.model.Player;
import wolf.model.stage.GameStage;

public class ListPlayersAction extends GameAction {

  public ListPlayersAction(GameStage stage) {
    super(stage, "players");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage("Alive players: " + getStage().getPlayers());
  }

  @Override
  public String getDescription() {
    return "Lists all players who are alive.";
  }

}
