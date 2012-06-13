package wolf.action.init;

import java.util.List;
import java.util.Map;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.role.GameRole;
import wolf.role.classic.Anarchist;
import wolf.role.classic.Civilian;
import wolf.role.classic.Priest;
import wolf.role.classic.Seer;
import wolf.role.classic.Vigilante;
import wolf.role.classic.Wolf;

import com.google.common.collect.Maps;

public class LoadPresetAction extends AbstractInitAction {

	private static Map<String, Preset> map = Maps.newLinkedHashMap();

	static {
		map.put("classic", new Preset().with(6, Civilian.class).with(2, Wolf.class).with(1, Seer.class));
		map.put("five",
				new Preset().with(1, Civilian.class).with(1, Wolf.class).with(1, Seer.class).with(1, Vigilante.class)
						.with(1, Anarchist.class));
		map.put("testing", new Preset().with(2, Civilian.class).with(1, Wolf.class).with(1, Seer.class).with(1, Priest.class));
	}

	public LoadPresetAction() {
		super(1);
	}

	@Override
	public String getCommandName() {
		return "load";
	}

	@Override
	protected void execute(WolfBot bot, String sender, String command, List<String> args) {
		String presetName = args.get(0);

		Preset preset = map.get(presetName.toLowerCase());

		if (preset == null) {
			throw new WolfException("There is no preset with the name: " + presetName);
		}

		Map<Class<? extends GameRole>, Integer> roleCountMap = initializer.getRoleCountMap();
		roleCountMap.clear();
		roleCountMap.putAll(preset.roleCountMap);
		bot.sendMessage("Loaded " + presetName.toLowerCase());
	}

	private static class Preset {

		private final Map<Class<? extends GameRole>, Integer> roleCountMap = Maps.newLinkedHashMap();

		public Preset with(int num, Class<? extends GameRole> role) {
			if (num > 0) {
				roleCountMap.put(role, num);
			} else {
				roleCountMap.remove(role);
			}
			return this;
		}

	}

}
