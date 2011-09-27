package wolf;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.jibble.pircbot.PircBot;
import org.jibble.pircbot.User;

import wolf.action.BotAction;
import wolf.action.InitGameAction;
import wolf.action.ShutdownAction;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class WolfBot extends PircBot {

	public static final ImmutableList<String> admins = ImmutableList.of("satnam", "semisober");
	public static final List<BotAction> actions = Lists.<BotAction> newArrayList(new InitGameAction(), new ShutdownAction());

	public static final String channel = "#mtgwolf";
	public static final String botName = "Overseer";

	private GameHandler currentHandler = null;

	public void transition(GameHandler nextHandler) {
		if (nextHandler == null) {
			throw new IllegalArgumentException();
		}

		this.currentHandler = nextHandler;
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		try {
			if (currentHandler != null) {
				currentHandler.onMessage(this, channel, sender, login, hostname, message);
				return;
			}
			handleMessage(this, actions, channel, sender, login, hostname, message);
		} catch (RuntimeException e) {
			e.printStackTrace();
			sendMessage(channel, "Problem: " + e.getMessage());
		}
	}

	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message) {
		try {
			if (currentHandler != null) {
				currentHandler.onPrivateMessage(this, sender, login, hostname, message);
				return;
			}
			handleMessage(this, actions, null, sender, login, hostname, message);
		} catch (Exception e) {
			e.printStackTrace();
			sendMessage(sender, "Problem: " + e.getMessage());
		}
	}

	public static void handleMessage(WolfBot bot, Collection<? extends BotAction> possibleActions, String channel, String sender,
			String login, String hostname, String message) {
		List<String> m = Lists.newArrayList(Splitter.on(' ').split(message));
		String command = m.get(0);

		if (!command.startsWith("!")) {
			return;
		}

		command = command.substring(1);

		BotAction targetAction = null;
		for (BotAction action : possibleActions) {
			if (action.matches(command)) {
				targetAction = action;
				break;
			}
		}

		if (targetAction != null) {
			targetAction.tryInvoke(bot, sender, command, m.subList(1, m.size()));
		} else {
			throw new RuntimeException("Unrecognized command: !" + command);
		}
	}

	public void deVoiceAll() {
		for (User user : getUsers(channel)) {
			deVoice(channel, user.getNick());
		}
	}

	public WolfBot() throws Exception {
		setName(botName);
		setLogin(getName());

		setVerbose(true);
		startIdentServer();

		connect("irc.efnet.nl");
		joinChannel(channel);
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		new WolfBot();
	}

}
