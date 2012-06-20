package wolf.role.classic;

import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.role.GameRole;

@DisplayName(value = "Anarchist", plural = "Anarchists")
public class Anarchist extends GameRole {

	@Override
	public Faction getFaction() {
		return Faction.WOLVES;
	}

	@Override
	public String onPeek() {
		return "Anarchist";
	}

}