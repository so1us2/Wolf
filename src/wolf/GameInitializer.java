package wolf;

import java.util.List;
import java.util.Map;

import wolf.action.init.AbstractInitAction;
import wolf.action.init.JoinAction;
import wolf.action.init.LeaveAction;
import wolf.action.init.LoadPresetAction;
import wolf.action.init.SetRoleCountAction;
import wolf.action.init.StartGameAction;
import wolf.role.GameRole;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * The GameInitializer is for setting up the game (everything that happens before the game actually begins).
 */
public class GameInitializer implements GameHandler {

	private List<AbstractInitAction> actions = Lists.newArrayList(new JoinAction(), new LeaveAction(), new LoadPresetAction(),
			new SetRoleCountAction(), new StartGameAction());

	private Map<String, Player> namePlayerMap = Maps.newLinkedHashMap();

	private Map<Class<? extends GameRole>, Integer> roleCountMap = Maps.newLinkedHashMap();

	public GameInitializer() {
		for (AbstractInitAction action : actions) {
			action.setInitializer(this);
		}
	}

	public Map<String, Player> getNamePlayerMap() {
		return namePlayerMap;
	}

	public Map<Class<? extends GameRole>, Integer> getRoleCountMap() {
		return roleCountMap;
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
