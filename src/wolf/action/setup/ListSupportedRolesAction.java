package wolf.action.setup;

import java.util.Collections;
import java.util.List;

import org.testng.collections.Lists;

import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.role.AbstractRole;
import wolf.model.stage.SetupStage;

public class ListSupportedRolesAction extends SetupAction {

  public ListSupportedRolesAction(SetupStage stage) {
    super(stage, "allroles");
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

    getBot().sendMessage("Village Roles:");
    for (AbstractRole r : (villageRoles)) {
      getBot().sendMessage(r.getType().name() + ": " + r.getDescription());
    }
    getBot().sendMessage("");
    getBot().sendMessage("Wolf Roles:");
    for (AbstractRole r : wolfRoles) {
      getBot().sendMessage(r.getType().name() + ": " + r.getDescription());
    }
  }

  @Override
  public String getDescription() {
    return "Prints out a brief description of all supported roles.";
  }

}
