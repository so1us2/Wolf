package wolf;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.action.init.AbstractInitAction;
import wolf.role.GameRole;

import com.google.common.collect.Lists;

public class WolfEngine implements GameHandler {

	private List<AbstractInitAction> actions = Lists.newArrayList();

	private final WolfBot bot;
	private final Map<String, Player> namePlayerMap;

	public WolfEngine(WolfBot bot, GameInitializer initializer) throws Exception {
		this.bot = bot;
		this.namePlayerMap = initializer.getNamePlayerMap();

		assignRoles(initializer.getRoleCountMap());
	}

	private void assignRoles(Map<Class<? extends GameRole>, Integer> roleCountMap) throws Exception {
		List<Player> playersWhoNeedRoles = Lists.newArrayList(namePlayerMap.values());
		outerLoop: for (Entry<Class<? extends GameRole>, Integer> entry : roleCountMap.entrySet()) {
			for (int i = 0; i < entry.getValue(); i++) {
				if (playersWhoNeedRoles.isEmpty()) {
					break outerLoop;
				}
				Player randomPlayer = playersWhoNeedRoles.remove((int) (Math.random() * playersWhoNeedRoles.size()));
				randomPlayer.setRole(entry.getKey().newInstance());
			}
		}

		bot.sendMessage("Assigning roles...");

		for (Player player : namePlayerMap.values()) {
			bot.sendMessage(player.getName(), "You are a " + player.getRole() + ".");
		}

		bot.sendMessage("Roles have been assigned.");
	}

	@Override
	public void onMessage(WolfBot bot, String channel, String sender, String login, String hostname, String message) {
		WolfBot.handleMessage(bot, actions, channel, sender, login, hostname, message);
	}

	@Override
	public void onPrivateMessage(WolfBot bot, String sender, String login, String hostname, String message) {
		bot.sendMessage(sender, "Private messages don't do anything right now.");
	}

}
