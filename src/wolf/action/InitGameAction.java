package wolf.action;

import java.util.List;

import wolf.GameInitializer;
import wolf.WolfBot;

public class InitGameAction extends BotAction {

	@Override
	public String getCommandName() {
		return "newgame";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		bot.deVoiceAll();

		bot.transition(new GameInitializer(bot));
		bot.sendMessage("New game is forming -- type !join");
		if (bot.getHandler() instanceof GameInitializer) {
			((GameInitializer) bot.getHandler()).startAdvertising();
		}
	}

}
