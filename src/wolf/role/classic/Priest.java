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
import wolf.engine.spell.ProtectSpell;
import wolf.role.GameRole;

import com.google.common.collect.ImmutableList;

@DisplayName(value = "Priest", plural = "Priests")
public class Priest extends GameRole {

	private Player currentProtectTarget;

	@Override
	protected Collection<? extends BotAction> getCurrentActions() {
		if (isNight()) {
			return ImmutableList.of(protectAction);
		} else {
			return super.getCurrentActions();
		}
	}

	@Override
	public void end(Time time) {
		if (time == Time.Night) {
			getEngine().cast(new ProtectSpell(currentProtectTarget));
		}
	}

	@Override
	protected void onNightEnds() {
		currentProtectTarget = null;
	}

	@Override
	public Faction getFaction() {
		return Faction.VILLAGERS;
	}

	@Override
	public boolean isFinished() {
		if (isNight() && currentProtectTarget == null) {
			return false;
		}

		return super.isFinished();
	}

	private final BotAction protectAction = new BotAction(1) {
		@Override
		public String getCommandName() {
			return "protect";
		}

		@Override
		protected void execute(WolfBot bot, String sender, String command, List<String> args) {
			Player target = getEngine().getPlayer(args.get(0));
			if (target == null) {
				throw new WolfException("No such player: " + args.get(0));
			}

			if (!target.isAlive()) {
				throw new WolfException("You can only protect players that are alive!");
			}

			currentProtectTarget = target;

			getEngine().getBot().sendMessage(sender, "Your wish to protect " + target.getName() + " has been received.");
		}
	};

}
