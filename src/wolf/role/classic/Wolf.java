package wolf.role.classic;

import java.util.Collection;
import java.util.List;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.action.BotAction;
import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.engine.Player;
import wolf.engine.Time;
import wolf.engine.spell.KillSpell;
import wolf.role.GameRole;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

@DisplayName(value = "Wolf", plural = "Wolves")
public class Wolf extends GameRole {

	private Player currentKillTarget = null;

	@Override
	public Faction getFaction() {
		return Faction.WOLVES;
	}

	@Override
	protected void onNightBegins() {
		getEngine().getBot().sendMessage(getPlayer(), "Tell me who you want to kill.  Message me '/kill [target]'");
	}

	@Override
	public void end(Time time, Collection<GameRole> wolves) {
		if (time == Time.Night) {
			// choose a random target from the collection of wolves
			Wolf chosen = (Wolf) Iterables.get(wolves, (int) Math.random() * wolves.size());
			Player killTarget = chosen.currentKillTarget;
			getEngine().cast(new KillSpell(killTarget));
		}
		super.end(time, wolves);
	}

	@Override
	protected void onNightEnds() {
		currentKillTarget = null;
	}

	@Override
	public boolean isFinished() {
		if (isNight() && currentKillTarget == null) {
			return false;
		}

		return true;
	}

	@Override
	protected Collection<? extends BotAction> getCurrentActions() {
		if (getEngine().getTime() == Time.Night) {
			return ImmutableList.of(killAction);
		} else {
			return super.getCurrentActions();
		}
	}

	private final BotAction killAction = new BotAction(1) {
		@Override
		public String getCommandName() {
			return "kill";
		}

		@Override
		protected void execute(WolfBot bot, String sender, String command, List<String> args) {
			Player killTarget = getEngine().getPlayer(args.get(0));
			if (killTarget == null) {
				throw new WolfException("No such player: " + args.get(0));
			}

			if (!killTarget.isAlive()) {
				throw new WolfException("You can only kill players that are alive!");
			}

			currentKillTarget = killTarget;

			getEngine().getBot().sendMessage(sender, "Your wish to kill " + killTarget.getName() + " has been received.");
		}
	};

}
