package wolf.role.classic;

import java.util.Collection;
import java.util.List;

import wolf.WolfBot;
import wolf.action.BotAction;
import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.engine.Time;
import wolf.role.GameRole;

import com.google.common.collect.ImmutableList;

@DisplayName(value = "Seer", plural = "Seers")
public class Seer extends GameRole {

	@Override
	public Faction getFaction() {
		return Faction.VILLAGE;
	}

	@Override
	protected void onNightBegins() {
		getEngine().getBot().sendMessage(getPlayer(), "Tell me who you want to peek.  Message me '/peek [target]'");
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
			String peekTarget = args.get(0);
		}
	};

}
