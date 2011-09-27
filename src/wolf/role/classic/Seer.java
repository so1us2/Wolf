package wolf.role.classic;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.action.BotAction;
import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.engine.Player;
import wolf.engine.Time;
import wolf.role.GameRole;

import com.google.common.collect.ImmutableList;

@DisplayName(value = "Seer", plural = "Seers")
public class Seer extends GameRole {

	private Player currentPeekTarget = null;
	private Map<Player, GameRole> peeks;

	public void Seer() {
		peeks = new HashMap();
	}

	@Override
	public Faction getFaction() {
		return Faction.VILLAGE;
	}

	@Override
	protected void onNightBegins() {
		getEngine().getBot().sendMessage(getPlayer(), "Tell me who you want to peek.  Message me '/peek [target]'");
	}

	@Override
	protected void onNightEnds() {
		// tell the seer the role of their peek target
		getEngine().getBot().sendMessage(getPlayer(), currentPeekTarget.getName() + " is a " + currentPeekTarget.getRole());
		peeks.put(currentPeekTarget, currentPeekTarget.getRole());
		currentPeekTarget = null;
	}

	@Override
	public boolean isFinished() {
		if (isNight() && currentPeekTarget == null) {
			return false;
		}

		return true;
	}

	@Override
	protected void onStatus() {
		super.onStatus();
		listPeeks();
	}

	private void listPeeks() {
		for (Player p : peeks.keySet())
			getEngine().getBot().sendMessage(getPlayer(), p.getName() + " is a " + peeks.get(p));
	}

	@Override
	protected Collection<? extends BotAction> getCurrentActions() {
		if (getEngine().getTime() == Time.Night) {
			return ImmutableList.of(peekAction);
		} else {
			return super.getCurrentActions();
		}
	}

	private final BotAction peekAction = new BotAction(1) {
		@Override
		public String getCommandName() {
			return "peek";
		}

		@Override
		protected void execute(WolfBot bot, String sender, String command, List<String> args) {
			if (command.equals("sitrep"))
				onStatus();
			else if (command.equals("peek")) {
				Player peekTarget = getEngine().getPlayer(args.get(0));
				if (peekTarget == null) {
					throw new WolfException("No such player: " + args.get(0));
				}

				if (!peekTarget.isAlive()) {
					throw new WolfException("You can only peek players that are alive!");
				}

				currentPeekTarget = peekTarget;

				getEngine().getBot()
						.sendMessage(sender, "Your peek has been received and will be revealed to you at the end of the night.");
			}
		}
	};

}
