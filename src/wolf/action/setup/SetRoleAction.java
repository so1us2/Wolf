package wolf.action.setup;

import java.util.List;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.SetupStage;

public class SetRoleAction extends SetupAction {

  public SetRoleAction(SetupStage stage) {
    super(stage, "set", "role", "number");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Role role; 
    try {
      role = Role.valueOf(args.get(0).toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new WolfException("There is no role \'" + args.get(0) + "\'.");
    }

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
  public String getDescription() {
    return "Set how many of a given role is to be included.";
  }

}
