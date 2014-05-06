package wolf.action.game;

import java.util.List;

import wolf.model.Player;
import wolf.model.stage.GameStage;

public class GetRecapAction extends GameAction {

  public GetRecapAction(GameStage stage) {
    super(stage, "recap");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    // TODO Auto-generated method stub

  }

  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

}
