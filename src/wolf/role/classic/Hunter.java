package wolf.role.classic;

import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.role.GameRole;

@DisplayName(value = "Hunter", plural = "Hunters")
public class Hunter extends GameRole {

	@Override
	public Faction getFaction() {
		return Faction.VILLAGERS;
	}

}
