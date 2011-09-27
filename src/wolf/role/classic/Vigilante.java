package wolf.role.classic;

import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.role.GameRole;

@DisplayName(value = "Vigilante", plural = "Vigilantes")
public class Vigilante extends GameRole {

	@Override
	public Faction getFaction() {
		return Faction.VILLAGE;
	}

}
