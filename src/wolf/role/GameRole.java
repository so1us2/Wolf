package wolf.role;

import java.util.List;
import java.util.Map;

import wolf.arch.Utils;
import wolf.engine.Faction;
import wolf.engine.Time;
import wolf.engine.WolfEngine;
import wolf.role.classic.Civilian;
import wolf.role.classic.Hunter;
import wolf.role.classic.Priest;
import wolf.role.classic.Seer;
import wolf.role.classic.Vigilante;
import wolf.role.classic.Wolf;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public abstract class GameRole {

	@SuppressWarnings("unchecked")
	public static final List<Class<? extends GameRole>> roles = Lists.newArrayList(Civilian.class, Hunter.class, Priest.class, Seer.class,
			Vigilante.class, Wolf.class);

	public static final Map<String, Class<? extends GameRole>> typeRoleMap = Maps.newHashMap();

	static {
		for (Class<? extends GameRole> c : roles) {
			typeRoleMap.put(c.getSimpleName().toLowerCase(), c);
		}
	}

	@Override
	public String toString() {
		return Utils.getDisplayName(getClass(), false);
	}

	public abstract Faction getFaction();

	public void begin(WolfEngine engine, Time time) {
		// Subclasses may override
	}

}
