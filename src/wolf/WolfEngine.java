package wolf;

import java.util.List;

import wolf.action.init.AbstractInitAction;

import com.google.common.collect.Lists;

public class WolfEngine implements GameHandler {

	private List<AbstractInitAction> actions = Lists.newArrayList();

	private final GameInitializer initializer;

	public WolfEngine(GameInitializer initializer) {
		this.initializer = initializer;
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
