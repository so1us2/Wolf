package wolf.action.init;

import java.util.List;
import java.util.Map.Entry;

import wolf.WolfBot;
import wolf.role.GameRole;

public class ListRolesAction extends AbstractInitAction {

	@Override
	public String getCommandName() {
		return "roles";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {

		if (initializer.getRoleCountMap().size() == 0) {
			bot.sendMessage("No roles have been added.");
			return;
		}

		StringBuilder sb = new StringBuilder();

		sb.append("Roles: ");

		for (Entry<Class<? extends GameRole>, Integer> entry : initializer.getRoleCountMap().entrySet()) {
			sb.append(entry.getKey().getName()).append("(").append(entry.getValue()).append(")").append(", ");
		}

		sb.delete(sb.length() - 2, sb.length());

		bot.sendMessage(sb.toString());
	}

}
