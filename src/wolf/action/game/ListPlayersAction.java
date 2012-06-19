package wolf.action.game;

import java.util.List;

import wolf.WolfBot;
import wolf.engine.Player;

public class ListPlayersAction extends AbstractGameAction {

	@Override
	public String getCommandName() {
		return "players";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {

		String msg = "";

		for (Player p : engine.getNamePlayerMap().values()) {
			msg = msg.concat(p.getName() + " ");
		}
		bot.sendMessage(WolfBot.channel, msg);
	}
}
