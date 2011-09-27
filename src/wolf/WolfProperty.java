package wolf;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WolfProperty {

	public final String name;
	public final ImmutableList<String> options;
	public String value;

	public WolfProperty(String name, String... options) {
		this.name = name;
		this.options = ImmutableList.copyOf(options);
		this.value = this.options.get(0);
	}

	public String getName() {
		return name;
	}

	public ImmutableList<String> getOptions() {
		return options;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public static Map<String, WolfProperty> createDefaults() {
		List<WolfProperty> defaults = Lists.newArrayList();

		defaults.add(new WolfProperty("StartingTime", "Day", "Night"));
		defaults.add(new WolfProperty("Silent", "Off", "On"));

		Map<String, WolfProperty> ret = Maps.newLinkedHashMap();
		for (WolfProperty prop : defaults) {
			ret.put(prop.name.toLowerCase(), prop);
		}
		return ret;
	}

}
