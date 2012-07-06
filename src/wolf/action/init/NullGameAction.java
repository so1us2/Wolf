package wolf.action.init;

import java.util.List;

import wolf.GameInitializer;
import wolf.WolfBot;

public class NullGameAction extends AbstractInitAction {

	@Override
	public String getCommandName() {
		return "nullgame";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		bot.sendMessage("Game has been cancelled!");
		if (bot.getHandler() instanceof GameInitializer) {
			((GameInitializer) bot.getHandler()).stopAdvertising();
		}
		bot.transition(null);
	}

}
