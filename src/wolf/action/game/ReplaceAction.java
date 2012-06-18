package wolf.action.game;

import java.util.List;

import wolf.WolfBot;
import wolf.WolfException;

public class ReplaceAction extends AbstractGameAction {

	@Override
	public String getCommandName() {
		return "replace";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		if (args.size() < 2) {
			throw new WolfException("Insufficient arguments.");
		}

		// if(engine.)

	}

}
