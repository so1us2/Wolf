package wolf.role.classic;

import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.engine.Time;
import wolf.engine.WolfEngine;
import wolf.role.GameRole;

@DisplayName(value = "Seer", plural = "Seers")
public class Seer extends GameRole {

	@Override
	public Faction getFaction() {
		return Faction.VILLAGE;
	}

	@Override
	public void begin(WolfEngine engine, Time time) {
		if (time == Time.Night) {
			// tell the seer to peek
		}
	}

}
