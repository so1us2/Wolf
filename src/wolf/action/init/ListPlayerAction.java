package wolf.action.init;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import wolf.Player;
import wolf.WolfBot;

import com.google.common.base.Joiner;

public class ListPlayerAction extends AbstractInitAction {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ListPlayerAction.class);

	@Override
	public String getCommandName() {
		return "players";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {

		Collection<Player> players = initializer.getNamePlayerMap().values();

		if (players.size() == 0) {
			bot.sendMessage("No players have joined.");
		} else {
			StringBuilder sb = new StringBuilder();
			bot.sendMessage(sb.append("Players: ").append(Joiner.on(", ").join(players)).toString());
		}
	}
}
