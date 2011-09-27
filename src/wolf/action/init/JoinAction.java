package wolf.action.init;

import java.util.List;
import java.util.Map;

import wolf.Player;
import wolf.WolfBot;

public class JoinAction extends AbstractInitAction {

	@Override
	public String getCommandName() {
		return "join";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		Map<String, Player> map = initializer.getNamePlayerMap();
		Player player = map.get(sender.toLowerCase());

		if (player != null) {
			throw new RuntimeException(sender + " has already joined.");
		}

		map.put(sender.toLowerCase(), new Player(sender));

		bot.deOp(WolfBot.channel, sender);
		bot.voice(WolfBot.channel, sender);

		bot.sendMessage(WolfBot.channel, sender + " joined the game.");
	}
}
