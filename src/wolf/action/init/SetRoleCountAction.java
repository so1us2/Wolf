package wolf.action.init;

import java.util.List;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.role.GameRole;

public class SetRoleCountAction extends AbstractInitAction {

	public SetRoleCountAction() {
		super(2);
	}

	@Override
	public String getCommandName() {
		return "setcount";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		String roleName = args.get(0);
		int num = Integer.valueOf(args.get(1));

		Class<? extends GameRole> role = GameRole.typeRoleMap.get(roleName.toLowerCase());

		if (role == null) {
			throw new WolfException("No such role: " + roleName);
		}

		if (num < 0) {
			throw new WolfException("Cannot have less than zero of a role!");
		}

		if (num == 0) {
			initializer.getRoleCountMap().remove(role);
		} else {
			initializer.getRoleCountMap().put(role, num);
		}
	}
}
