package wolf.action.init;

import java.util.List;
import java.util.Map;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.engine.Player;

public class KickPlayerAction extends AbstractInitAction {

	public KickPlayerAction() {
		super(1);
	}

	@Override
	public String getCommandName() {
		return "kick";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		Map<String, Player> map = initializer.getNamePlayerMap();

		if (map.remove(args.get(0)) == null) {
			throw new WolfException(args.get(0) + " is not in the game.");
		}

		bot.sendMessage(sender + " kicked " + args.get(0) + ".");
	}

	@Override
	protected boolean requiresAdmin() {
		return true;
	}
}
