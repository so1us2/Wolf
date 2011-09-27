package wolf.role.classic;

import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.role.GameRole;

@DisplayName(value = "Wolf", plural = "Wolves")
public class Wolf extends GameRole {

	@Override
	public Faction getFaction() {
		return Faction.WOLVES;
	}

}
