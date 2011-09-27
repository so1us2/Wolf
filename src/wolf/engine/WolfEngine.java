package wolf.engine;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.GameHandler;
import wolf.GameInitializer;
import wolf.WolfBot;
import wolf.WolfException;
import wolf.action.init.AbstractInitAction;
import wolf.role.GameRole;

import com.google.common.collect.Lists;

public class WolfEngine implements GameHandler {

	private List<AbstractInitAction> actions = Lists.newArrayList();

	private final WolfBot bot;

	private final Map<String, Player> namePlayerMap;
	private final Map<String, WolfProperty> properties;

	private Time time;
	int dayNumber = 0;

	private Faction winner = null;

	public WolfEngine(WolfBot bot, GameInitializer initializer) throws Exception {
		this.bot = bot;
		this.namePlayerMap = initializer.getNamePlayerMap();
		this.properties = initializer.getProperties();

		Time startingTime = properties.get(WolfProperty.STARTING_TIME).getValue();

		assignRoles(initializer.getRoleCountMap());

		begin(startingTime);
	}

	private void begin(Time time) {
		if (time == null) {
			throw new IllegalArgumentException("time can't be null.");
		}

		this.time = time;

		if (time == Time.Day) {
			dayNumber++;
			bot.sendMessage("Day " + dayNumber + " dawns on the village.");
		} else {
			bot.sendMessage("The world grows dark as the villagers drift to sleep.");
		}

		for (Player player : namePlayerMap.values()) {
			player.begin(this, time);
		}
	}

	private void assignRoles(Map<Class<? extends GameRole>, Integer> roleCountMap) throws Exception {
		List<Player> playersWhoNeedRoles = Lists.newArrayList(namePlayerMap.values());
		outerLoop: for (Entry<Class<? extends GameRole>, Integer> entry : roleCountMap.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				if (playersWhoNeedRoles.isEmpty()) {
					break outerLoop;
				}
				Player randomPlayer = playersWhoNeedRoles.remove((int) (Math.random() * playersWhoNeedRoles.size()));
				GameRole role = entry.getKey().newInstance();
				role.setEngine(this);
				randomPlayer.setRole(role);
			}
		}

		bot.sendMessage("Assigning roles...");

		for (Player player : namePlayerMap.values()) {
			bot.sendMessage(player, "You are a " + player.getRole() + ".");
		}

		bot.sendMessage("Roles have been assigned.");
	}

	@Override
	public void onMessage(WolfBot bot, String channel, String sender, String login, String hostname, String message) {
		WolfBot.handleMessage(bot, actions, channel, sender, message);
	}

	@Override
	public void onPrivateMessage(WolfBot bot, String sender, String login, String hostname, String message) {
		Player player = getPlayer(sender);
		if (player == null) {
			throw new WolfException("You are not part of the game.");
		}

		player.getRole().handlePrivateMessage(message);
	}

	private Player getPlayer(String sender) {
		return namePlayerMap.get(sender.toLowerCase());
	}

	public Time getTime() {
		return time;
	}

	public WolfBot getBot() {
		return bot;
	}

}
