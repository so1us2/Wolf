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

@DisplayName(value = "Vigilante", plural = "Vigilantes")
public class Vigilante extends GameRole {

	boolean shotFired = false;

	@Override
	public Faction getFaction() {
		return Faction.VILLAGERS;
	}

	@Override
	protected void onNightBegins() {
		if (!shotFired)
			getEngine().getBot().sendMessage(getPlayer(), "Tell me who you want to snipe.  Message me 'snipe [target] OR holdfire'");
	}

	@Override
	protected Collection<? extends BotAction> getCurrentActions() {
		if (getEngine().getTime() == Time.Night) {
			return ImmutableList.of(snipeAction);
		} else {
			return super.getCurrentActions();
		}
	}

	private final BotAction snipeAction = new BotAction(1) {
		@Override
		public String getCommandName() {
			return "snipe";
		}

		@Override
		protected void execute(WolfBot bot, String sender, String command, List<String> args) {
			String peekTarget = args.get(0);
		}
	};

	private final BotAction holdAction = new BotAction(1) {
		@Override
		public String getCommandName() {
			return "snipe";
		}

		@Override
		protected void execute(WolfBot bot, String sender, String command, List<String> args) {
			String peekTarget = args.get(0);
		}
	};

}
