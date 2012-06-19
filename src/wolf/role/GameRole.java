package wolf.role;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import wolf.WolfBot;
import wolf.WolfException;
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

	private Player voteTarget;

	private boolean isProtected = false;

	public abstract Faction getFaction();

	protected void onDayBegins() {
		isProtected = false;

		// Subclasses may override
	}

	protected void onDayEnds() {
		voteTarget = null;
	}

	protected void onNightBegins() {
		// Subclasses may override
	}

	protected void onNightEnds() {
		// Subclasses may override
	}

	protected void onStatus() {
		getEngine().getBot().sendMessage(getPlayer(), "You are a " + getPlayer().getRole());
	}

	protected Collection<? extends BotAction> getCurrentActions() {
		// Subclasses may override
		if (isDay()) {
			return Collections.singletonList(voteAction);
		} else {
			return Collections.emptyList();
		}
	}

	public boolean isFinished() {
		if (isDay()) {
			return voteTarget != null;
		}

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

	public void end(Time time, Collection<GameRole> members) {
		for (GameRole member : members) {
			member.end(time);
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

	public void setVoteTarget(Player voteTarget) {
		this.voteTarget = voteTarget;
	}

	public Player getVoteTarget() {
		return voteTarget;
	}

	public void setProtected(boolean isProtected) {
		this.isProtected = isProtected;
	}

	public boolean isProtected() {
		return isProtected;
	}

	public void sendHistory() {

	}

	@Override
	public String toString() {
		return Utils.getDisplayName(getClass(), false);
	}

	private final BotAction voteAction = new BotAction(1) {
		@Override
		public String getCommandName() {
			return "vote";
		}

		@Override
		protected void execute(WolfBot bot, String sender, String command, List<String> args) {
			Player voteTarget = getEngine().getPlayer(args.get(0));

			if (voteTarget == null) {
				throw new WolfException("No such player: " + args.get(0));
			}

			if (!voteTarget.isAlive()) {
				throw new WolfException("You can only vote players that are alive!");
			}

			if (voteTarget == GameRole.this.voteTarget) {
				throw new WolfException("You've already voted for " + voteTarget);
			}

			Player oldVoteTarget = getVoteTarget();

			setVoteTarget(voteTarget);
			getEngine().getVotingHistory().record(getPlayer(), voteTarget);

			getEngine().getBot().sendMessage(sender, "Your vote for " + voteTarget.getName() + " has been received.");

			if (oldVoteTarget == null) {
				getEngine().getBot().sendMessage("Someone voted.");
			} else {
				getEngine().getBot().sendMessage("Someone changed their vote.");
			}
		}
	};

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
