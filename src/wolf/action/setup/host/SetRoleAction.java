package wolf.action.setup.host;

import java.util.List;

import wolf.WolfException;
import wolf.action.setup.SetupAction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.SetupStage;

public class SetRoleAction extends SetupAction {

  public SetRoleAction(SetupStage stage) {
    super(stage, "setrole", "role", "number");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Role role = Role.parse(args.get(0));

    int num;
    try {
      num = Integer.parseInt(args.get(1));
    } catch (NumberFormatException e) {
      throw new WolfException(args.get(1) + " is not a valid number.");
    }

    getStage().getConfig().setRole(role, num);
    getBot().sendMessage(role + " set to " + num + ".");
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

  @Override
  public String getDescription() {
    return "Set how many of a given role is to be included.";
  }

}
