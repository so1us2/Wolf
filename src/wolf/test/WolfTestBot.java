package wolf.test;

import java.util.List;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.jibble.pircbot.User;

import wolf.engine.Player;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class WolfTestBot extends org.jibble.pircbot.PircBot {

	public static final ImmutableList<String> admins = ImmutableList.of("satnam", "semisober");

	public static final String channel = "#mtgwolf";
	public static final int numTesters = 5;
	public String botName;

	public WolfTestBot() throws Exception {

		Random r = new Random();

		botName = new String("monkey" + r.nextInt(100));

		setName(botName);
		setLogin(getName());

		setVerbose(true);
		startIdentServer();

		connect("irc.efnet.nl");
		joinChannel(channel);
		messageAdmins(botName + " reporting for duty.");
	}

	/**
	 * Sends a message to everyone in the wolf channel.
	 */
	public void sendMessage(String message) {
		super.sendMessage(channel, message);
	}

	public void messageAdmins(String message) {
		for (User u : getUsers(channel)) {
			if (admins.contains(u.getNick())) {
				sendMessage(u.getNick(), message);
			}
		}
	}

	public void sendMessage(Player player, String message) {
		super.sendMessage(player.getName(), message);
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {

		List<String> m = Lists.newArrayList(Splitter.on(' ').split(message));
		String command = m.get(0);

		if (admins.contains(sender)) {
			if (command.equals("!monkeys")) {
				sendMessage(message.substring(message.indexOf(" ")));
			}
		}
	}

	@Override
	public void onPrivateMessage(String sender, String login, String hostname, String message) {

		List<String> m = Lists.newArrayList(Splitter.on(' ').split(message));
		String command = m.get(0);

		int numBots = 1;

		try {
			numBots = Integer.valueOf(m.get(1));
		} catch (Exception e) {
			numBots = 1;
		}

		if (admins.contains(sender)) {
			if (command.equals("!newmonkey")) {
				try {
					for (int i = 0; i < numBots; i++) {
						new WolfTestBot();
					}
				} catch (Exception e) {
				}
				sendMessage(sender, "Creating " + numBots + " monkeys!");
			} else if (command.equals("!message")) {
				sendMessage(m.get(1), message.substring(message.indexOf(m.get(1)) + m.get(1).length()));
			} else {
				sendMessage(message);
			}
		} else {
			messageAdmins(sender + ": " + message);
		}
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		new WolfTestBot();
	}

}
