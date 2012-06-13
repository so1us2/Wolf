package wolf.test;

import java.util.List;
import java.util.Random;

import org.apache.log4j.BasicConfigurator;
import org.jibble.pircbot.User;

import wolf.engine.Player;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class MonkeyBot extends org.jibble.pircbot.PircBot {

	public static final ImmutableList<String> admins = ImmutableList.of("satnam", "semisober");

	public static final String[] serverList = { "efnet.bredband2.se", "irc.teksavvy.ca", "efnet.port80.se", "irc.du.se", "irc.efnet.nl",
			"irc.homelien.no", "irc.choopa.net", "irc.colosolutions.net ", "irc.prison.net", "irc.eversible.com", "irc.mzima.net" };

	public static final String channel = "#mtgwolf_test";

	public static final int numTesters = 1;
	public String botName;

	public MonkeyBot() throws Exception {
		init(getRandomServer());
	}

	public MonkeyBot(boolean child) throws Exception {
		init(getRandomServer());
		if (child)
			new MonkeyBot(child);
	}

	public MonkeyBot(boolean child, String server) throws Exception {
		init(server);
		if (child)
			new MonkeyBot(child, server);
	}

	private String getRandomServer() {
		return serverList[10];
		// Random r = new Random();
		//
		// return serverList[r.nextInt(serverList.length)];
	}

	private void init(String server) throws Exception {
		Random r = new Random();

		botName = new String("monkey" + r.nextInt(100));

		setName(botName);
		setLogin(getName());

		setVerbose(true);
		startIdentServer();

		connect(server);
		// "irc.efnet.nl"
		joinChannel(channel);
	}

	@Override
	protected void onJoin(String channel, String sender, String login, String hostname) {
		// messageAdmins(botName + " reporting for duty.");
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
				sendMessage(message.substring(message.indexOf(" ") + 1));
			} else if (command.equals("!takeover")) {
				monkeyTakeover();
			} else if (command.equals("!message")) {
				sendMessage(m.get(1), message.substring(message.indexOf(m.get(1)) + m.get(1).length() + 1));
			} else if (command.equals("!newmonkey")) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							new MonkeyBot();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
				t.setDaemon(true);
				t.start();
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
				for (int i = 0; i < numBots; i++) {
					Thread t = new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								new MonkeyBot();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});
					t.setDaemon(true);
					t.start();
				}
				sendMessage(sender, "Creating " + numBots + " monkeys!");
			} else if (command.equals("!message")) {
				sendMessage(m.get(1), message.substring(message.indexOf(m.get(1)) + m.get(1).length() + 1));
			} else if (command.equals("!takeover")) {
				monkeyTakeover();
			} else {
				sendMessage(message);
			}
		} else {
			messageAdmins(sender + ": " + message);
		}
	}

	private void monkeyTakeover() {
		// for (User u : getUsers(channel)) {
		// if (u.getNick().contains("monkey") || admins.contains(u.getNick()))
		// this.op(channel, u.getNick());
		// else if (u.isOp())
		// this.deOp(channel, u.getNick());
		// }
	}

	public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();

		for (int i = 0; i < MonkeyBot.numTesters; i++) {
			new MonkeyBot(false);
		}
	}

}
