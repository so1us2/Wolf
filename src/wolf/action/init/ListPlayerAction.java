package wolf.action.init;

import java.util.Collection;
import java.util.List;

import wolf.Player;
import wolf.WolfBot;

public class ListPlayerAction extends AbstractInitAction {

	@Override
	public String getCommandName() {
		return "players";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {

		Collection<Player> players = initializer.getNamePlayerMap().values();

		for (Player p : players) {
			bot.sendMessage(p.getName());
		}
	}

}
