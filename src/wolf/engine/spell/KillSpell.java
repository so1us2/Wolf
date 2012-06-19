package wolf.engine.spell;

import java.util.Map;
import java.util.Map.Entry;

import wolf.engine.Player;
import wolf.engine.WolfEngine;

import com.google.common.collect.Maps;

public class KillSpell extends Spell {

	private final Player target;
	private Map<Player, String> messages = Maps.newHashMap();

	public KillSpell(Player target) {
		this.target = target;
	}

	public Player getTarget() {
		return target;
	}

	public Map<Player, String> getMessages() {
		return messages;
	}

	@Override
	public void execute(WolfEngine engine) {
		if (!target.isAlive()) {
			return;
		}

		if (target.getRole().isProtected()) {
			engine.getBot().sendMessage("No one died.");
			return;
		}

		target.kill();

		engine.getBot().sendMessage(target.getName() + " was killed.");

		for (Entry<Player, String> e : messages.entrySet()) {
			engine.getBot().sendMessage(e.getKey(), e.getValue());
		}
	}

}
