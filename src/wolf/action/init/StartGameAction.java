package wolf.action.init;

import java.util.List;
import java.util.Map;

import wolf.WolfBot;
import wolf.WolfEngine;
import wolf.WolfException;
import wolf.role.GameRole;

public class StartGameAction extends AbstractInitAction {

	@Override
	public String getCommandName() {
		return "start";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		Map<Class<? extends GameRole>, Integer> roleCounts = initializer.getRoleCountMap();
		int numPlayers = initializer.getNamePlayerMap().size();

		int neededPlayers = 0;
		for (Integer val : roleCounts.values()) {
			neededPlayers += val;
		}

		if (numPlayers < neededPlayers) {
			throw new WolfException("You only have " + numPlayers + ", but you need " + neededPlayers + " to start the game.");
		}

		if (numPlayers > neededPlayers) {
			throw new WolfException("You currently have " + numPlayers + ", which is too many. Once there are " + neededPlayers
					+ " you may start the game.");
		}

		bot.transition(new WolfEngine(initializer));
	}
}
