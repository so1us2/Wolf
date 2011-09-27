package wolf.engine.spell;

import wolf.engine.Player;
import wolf.engine.WolfEngine;

public class KillSpell implements Spell {

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

		target.kill();
	}

}
