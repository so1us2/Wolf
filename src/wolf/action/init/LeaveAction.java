package wolf.action.init;

import java.util.List;
import java.util.Map;

import wolf.Player;
import wolf.WolfBot;
import wolf.WolfException;

public class LeaveAction extends AbstractInitAction {

	@Override
	public String getCommandName() {
		return "leave";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		Map<String, Player> map = initializer.getNamePlayerMap();
		Player player = map.get(sender.toLowerCase());

		if (player == null) {
			throw new WolfException(sender + " can't leave a game they haven't joined.");
		}

		map.remove(sender.toLowerCase());
		bot.deVoice(WolfBot.channel, sender);
		bot.sendMessage(sender + " left the game.");
	}

}
