package wolf;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import wolf.action.init.AbstractInitAction;
import wolf.action.init.JoinAction;
import wolf.action.init.KickPlayerAction;
import wolf.action.init.LeaveAction;
import wolf.action.init.ListPlayerAction;
import wolf.action.init.ListRolesAction;
import wolf.action.init.LoadPresetAction;
import wolf.action.init.NullGameAction;
import wolf.action.init.PregameStatusAction;
import wolf.action.init.SetRoleCountAction;
import wolf.action.init.StartGameAction;
import wolf.engine.Player;
import wolf.engine.WolfProperty;
import wolf.role.GameRole;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The GameInitializer is for setting up the game (everything that happens before the game actually begins).
 */
public class GameInitializer implements GameHandler {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(GameInitializer.class);

	private final List<AbstractInitAction> actions = Lists.newArrayList(new JoinAction(), new LeaveAction(), new LoadPresetAction(),
			new SetRoleCountAction(), new StartGameAction(), new PregameStatusAction(), new ListPlayerAction(), new ListRolesAction(),
			new NullGameAction(), new KickPlayerAction());

	private final WolfBot bot;

	private final Map<String, Player> namePlayerMap = Maps.newLinkedHashMap();

	private final Map<Class<? extends GameRole>, Integer> roleCountMap = Maps.newLinkedHashMap();

	private final Map<String, WolfProperty> properties = WolfProperty.createDefaults();

	public GameInitializer(WolfBot newBot) {
		for (AbstractInitAction action : actions) {
			action.setInitializer(this);
		}

		this.bot = newBot;

		Thread t = new Thread() {
			@Override
			public void run() {
				while (bot.getHandler() instanceof GameInitializer) {
					// do announcement
					try {
						Thread.sleep(15000); // sleep 1 second
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};

		t.run();
	}

	public Map<String, WolfProperty> getProperties() {
		return properties;
	}

	public Map<String, Player> getNamePlayerMap() {
		return namePlayerMap;
	}

	public Map<Class<? extends GameRole>, Integer> getRoleCountMap() {
		return roleCountMap;
	}

	@Override
	public void onMessage(WolfBot bot, String channel, String sender, String login, String hostname, String message) {
		WolfBot.handleMessage(bot, actions, channel, sender, message);
	}

	@Override
	public void onPrivateMessage(WolfBot bot, String sender, String login, String hostname, String message) {
		bot.sendMessage(sender, "Private messages don't do anything right now.");
	}

	@Override
	public void onPart(WolfBot bot, String channel, String sender, String login, String hostname) {

		bot.sendMessage("sender: " + sender + " login: " + login);
		if (namePlayerMap.remove(sender) != null) {
			bot.sendMessage(sender + " has left the game.");
		}
	}

}
