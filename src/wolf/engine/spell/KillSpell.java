package wolf.engine.spell;

import java.util.Map;
import java.util.Map.Entry;

import wolf.engine.Player;
import wolf.engine.Time;
import wolf.engine.WolfEngine;

import com.google.common.collect.Maps;

public class KillSpell extends Spell {

	private final Player target;
	private final Map<Player, String> messages = Maps.newHashMap();

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

		if (engine.getTime().equals(Time.Night)) {
			engine.getBot().sendMessage(target.getName() + target.getRole().diedAtNightNotice());
		}

		for (Entry<Player, String> e : messages.entrySet()) {
			engine.getBot().sendMessage(e.getKey(), e.getValue());
		}
	}

}
