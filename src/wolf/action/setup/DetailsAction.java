package wolf.action.setup;

import java.util.List;

import wolf.WolfException;
import wolf.action.Action;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.role.AbstractRole;
import wolf.model.stage.SetupStage;

public class DetailsAction extends SetupAction {

  public DetailsAction(SetupStage stage) {
    super(stage, "details", "role");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Role role;
    try {
      role = Role.valueOf(args.get(0).toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new WolfException("There is no role \'" + args.get(0) + "\'.");
    }

    AbstractRole instance = AbstractRole.create(role, null);
    getBot().sendMessage(role.name() + " - Team " + instance.getVictoryTeamFaction());
    getBot().sendMessage(instance.getDescription());

    List<Action> actions = instance.getNightActions();
    if (actions.isEmpty()) {
      getBot().sendMessage("No special actions.");
    } else {
      for (Action a : actions) {
        getBot().sendMessage("!" + a.getName() + ": " + a.getDescription());
      }
    }
  }

  @Override
  public String getDescription() {
    return "Print out the details for a given role.";
  }

}
