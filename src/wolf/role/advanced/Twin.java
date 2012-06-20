package wolf.role.advanced;

import java.util.List;

import wolf.WolfBot;
import wolf.arch.DisplayName;
import wolf.engine.Faction;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

@DisplayName(value = "Twin", plural = "Twins")
public class Twin extends wolf.role.GameRole {

	@Override
	public Faction getFaction() {
		return Faction.VILLAGERS;
	}

	@Override
	public String onPeek() {
		return "Twin";
	}

	@Override
	public void handlePrivateMessage(String message) {
		List<String> m = Lists.newArrayList(Splitter.on(' ').split(message));
		String command = m.get(0);

		if (isNight()) {
			if (!command.startsWith("!")) {
				getEngine().roleChat(this.getClass(), getPlayer(), "<TwinChat>", message);
				return;
			}
		}
		WolfBot.handleMessage(getEngine().getBot(), getCurrentActions(), null, getPlayer().getName(), message);
	}

}
