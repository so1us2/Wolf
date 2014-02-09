package wolf.action.setup.admin;

import java.util.List;

import wolf.action.setup.SetupAction;
import wolf.model.Player;
import wolf.model.stage.SetupStage;

public class SetHostAction extends SetupAction {

  public SetHostAction(SetupStage stage) {
    super(stage, "sethost", "newhost");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Player oldHost = getStage().getHost();
    getStage().setHost(getStage().getPlayer(args.get(0)));
    if (oldHost != null) {
      getStage().getBot().sendMessage(oldHost.getName(),
          "You have been removed as host by an administrator.");
    }
    getStage().getBot().sendMessage(invoker.getName(),
        "You have appointed " + getStage().getHost().getName() + ".");
  }

  @Override
  public String getDescription() {
    return "Set a new host for the game.";
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

}
