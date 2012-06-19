package wolf.action.game;

import java.util.List;
import java.util.Map;

import org.jibble.pircbot.User;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.engine.Player;

public class ReplaceAction extends AbstractGameAction {

	public ReplaceAction() {
		super(2);
	}

	@Override
	public String getCommandName() {
		return "replace";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {

		if (engine.getPlayer(args.get(1)) != null) {
			throw new WolfException(args.get(1) + " is already in the game.");
		}

		// check if subbed in player is in channel -- has to be easier way. No Contains method?
		boolean found = false;

		for (User u : bot.getUsers(WolfBot.channel)) {
			if (u.getNick().equals(args.get(1))) {
				found = true;
			}
		}

		if (!found) {
			throw new WolfException("Cannot sub in " + args.get(1) + " as player is not in the game channel.");
		}

		Map<String, Player> map = engine.getNamePlayerMap();
		Player player = map.remove(args.get(0).toLowerCase());

		if (player == null) {
			throw new WolfException(args.get(0) + " not in game.");
		}

		Player newPlayer = new Player(args.get(1));
		newPlayer.setRole(player.getRole());

		map.put(args.get(1).toLowerCase(), newPlayer);

		bot.deOp(WolfBot.channel, args.get(1));
		bot.voice(WolfBot.channel, args.get(1));
		bot.deVoice(WolfBot.channel, args.get(0));

		bot.sendMessage(args.get(1), "You have replaced " + args.get(0) + ". You are a " + newPlayer.getRole() + ".");
		// If player is taking over a role that has info, need to share that info.
		newPlayer.getRole().sendHistory();

		bot.sendMessage(args.get(1) + " has replaced " + args.get(0) + ".");

	}
}
