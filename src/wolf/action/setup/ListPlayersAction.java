package wolf.action.setup;

import java.util.List;

import wolf.model.GameModel;
import wolf.model.GameSetupStage;
import wolf.model.Player;

public class ListPlayersAction extends SetupAction {

  public ListPlayersAction(GameSetupStage stage) {
    super(stage, "players", 0);
  }

  @Override
  protected void execute(GameModel model, Player invoker, List<String> args) {
    String s = getStage().getPlayers().toString();
  }

}
