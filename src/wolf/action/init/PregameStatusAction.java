package wolf.action.init;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import wolf.WolfBot;
import wolf.role.GameRole;

public class PregameStatusAction extends AbstractInitAction {

	@Override
	public String getCommandName() {
		return "status";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {

		Map<Class<? extends GameRole>, Integer> roleCounts = initializer.getRoleCountMap();
		int numPlayers = initializer.getNamePlayerMap().size();

		bot.sendMessage("You have " + numPlayers + " players. You need " + roleCounts.size() + "total players.");
		printRoles(bot);
	}

	protected void printRoles(WolfBot bot) {

		Set<Entry<Class<? extends GameRole>, Integer>> roleCounts = initializer.getRoleCountMap().entrySet();

		for (Entry<Class<? extends GameRole>, Integer> entry : initializer.getRoleCountMap().entrySet()) {
			bot.sendMessage(entry.getKey() + ": " + entry.getValue());
		}

	}
}
