package wolf.role;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import wolf.WolfBot;
import wolf.action.BotAction;
import wolf.arch.Utils;
import wolf.engine.Faction;
import wolf.engine.Player;
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

	private WolfEngine engine;
	private Player player;

	public abstract Faction getFaction();

	protected void onDayBegins() {
		// Subclasses may override
	}

	protected void onDayEnds() {
		// Subclasses may override
	}

	protected void onNightBegins() {
		// Subclasses may override
	}

	protected void onNightEnds() {
		// Subclasses may override
	}

	protected Collection<? extends BotAction> getCurrentActions() {
		// Subclasses may override
		return Collections.emptyList();
	}

	public boolean isFinished() {
		// Subclasses may override
		return true;
	}

	public void handlePrivateMessage(String message) {
		WolfBot.handleMessage(engine.getBot(), getCurrentActions(), null, player.getName(), message);
	}

	public void begin(Time time) {
		if (time == Time.Day) {
			onDayBegins();
		} else if (time == Time.Night) {
			onNightBegins();
		}
	}

	public void end(Time time) {
		if (time == Time.Day) {
			onDayEnds();
		} else if (time == Time.Night) {
			onNightEnds();
		}
	}

	protected final boolean isDay() {
		return getEngine().getTime() == Time.Day;
	}

	protected final boolean isNight() {
		return getEngine().getTime() == Time.Night;
	}

	public final Player getPlayer() {
		return player;
	}

	public final WolfEngine getEngine() {
		return engine;
	}

	public final void setPlayer(Player player) {
		this.player = player;
	}

	public final void setEngine(WolfEngine engine) {
		this.engine = engine;
	}

	@Override
	public String toString() {
		return Utils.getDisplayName(getClass(), false);
	}

	@SuppressWarnings("unchecked")
	public static final List<Class<? extends GameRole>> roles = Lists.newArrayList(Civilian.class, Hunter.class, Priest.class, Seer.class,
			Vigilante.class, Wolf.class);

	public static final Map<String, Class<? extends GameRole>> typeRoleMap = Maps.newHashMap();

	static {
		for (Class<? extends GameRole> c : roles) {
			typeRoleMap.put(c.getSimpleName().toLowerCase(), c);
		}
	}

}
