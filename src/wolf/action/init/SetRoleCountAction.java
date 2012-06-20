package wolf.action.init;

import java.util.List;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.arch.Utils;
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
	public String getHelperText() {

		String msg = "Supported roles: ";

		for (String s : GameRole.typeRoleMap.keySet()) {
			msg = msg.concat(s + ", ");
		}

		return msg;
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
			bot.sendMessage("Removed role: " + role.getSimpleName());
		} else {
			initializer.getRoleCountMap().put(role, num);
			bot.sendMessage("Set the number of " + Utils.getDisplayName(role, true) + " to " + num + ".");
		}
	}
}
