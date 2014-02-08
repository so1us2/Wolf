package wolf.action.setup;

import java.util.Collections;
import java.util.List;

import org.testng.collections.Lists;

import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.role.AbstractRole;
import wolf.model.stage.SetupStage;

public class ListAllRolesAction extends SetupAction {

  public ListAllRolesAction(SetupStage stage) {
    super(stage, "roles");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {

    List<AbstractRole> villageRoles = Lists.newArrayList();
    List<AbstractRole> wolfRoles = Lists.newArrayList();

    for (Role r : Role.values()) {
      AbstractRole instance = AbstractRole.create(r, null);
      if (instance.getVictoryTeamFaction() == Faction.VILLAGERS) {
        villageRoles.add(instance);
      } else if (instance.getVictoryTeamFaction() == Faction.WOLVES) {
        wolfRoles.add(instance);
      }
    }

    Collections.sort(villageRoles);
    Collections.sort(wolfRoles);

    StringBuilder output = new StringBuilder();
    output.append("Village Roles: ");
    for (AbstractRole r : (villageRoles)) {
      output.append(r.getType().name()).append(", ");
    }
    output.setLength(output.length() - 2);
    getBot().sendMessage(invoker.getName(), output.toString());
    getBot().sendMessage(invoker.getName(), "");
    output = new StringBuilder();
    output.append("Wolf Roles: ");
    for (AbstractRole r : wolfRoles) {
      output.append(r.getType().name()).append(", ");
    }
    output.setLength(output.length() - 2);
    getBot().sendMessage(invoker.getName(), output.toString());
    getBot().sendMessage(invoker.getName(), "For more information on a role, /details <role>");
  }

  @Override
  public String getDescription() {
    return "Prints out a brief description of all supported roles.";
  }

}
