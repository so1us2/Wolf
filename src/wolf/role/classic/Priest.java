package wolf.role.classic;

import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.role.GameRole;

@DisplayName(value = "Priest", plural = "Priests")
public class Priest extends GameRole {

	@Override
	public Faction getFaction() {
		return Faction.VILLAGE;
	}

}
