package wolf.engine.spell;

import wolf.engine.Player;
import wolf.engine.WolfEngine;

public class KillSpell extends Spell {

	private final Player target;

	public KillSpell(Player target) {
		this.target = target;
	}

	public Player getTarget() {
		return target;
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
	}

}
