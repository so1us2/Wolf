package wolf.action.setup;

import java.util.List;

import wolf.WolfException;
import wolf.model.GameModel;
import wolf.model.GameSetupStage;
import wolf.model.Player;

public class JoinAction extends SetupAction {

  public JoinAction(GameSetupStage stage) {
    super(stage, "join", 0);
  }

  @Override
  protected void execute(GameModel model, Player invoker, List<String> args) {
    boolean added = getStage().getPlayers().add(invoker);
    if (!added) {
      throw new WolfException(invoker.getName() + " already joined!");
    }
    model.getBot().sendMessage(invoker.getName() + " joined the game.");
  }

}
