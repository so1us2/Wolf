package wolf.action.game;

import java.util.List;

import wolf.model.Player;
import wolf.model.stage.GameStage;

public class GetRoleAction extends GameAction {

  public GetRoleAction(GameStage stage) {
    super(stage, "getRole");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage(invoker.getName(), "You are a " + invoker.getRole().getType().name());
  }

  @Override
  public String getDescription() {
    return "Gets your role.";
  }

}
