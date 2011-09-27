package wolf.engine;

import java.util.List;
import java.util.Map;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class WolfProperty {

	public static String STARTING_TIME = "StartingTime", SILENT = "Silent";

	public final String name;
	public final ImmutableList<Object> options;
	public Object value;

	/**
	 * Note that the first property in the options array is used as the default starting option.
	 */
	public WolfProperty(String name, Object... options) {
		this.name = name;
		this.options = ImmutableList.copyOf(options);
		this.value = this.options.get(0);
	}

	public String getName() {
		return name;
	}

	public ImmutableList<Object> getOptions() {
		return options;
	}

	@SuppressWarnings("unchecked")
	public <T> T getValue() {
		return (T) value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public static Map<String, WolfProperty> createDefaults() {
		List<WolfProperty> defaults = Lists.newArrayList();

		defaults.add(new WolfProperty(STARTING_TIME, Time.Day, Time.Night));
		defaults.add(new WolfProperty(SILENT, false, true));

		Map<String, WolfProperty> ret = Maps.newLinkedHashMap();
		for (WolfProperty prop : defaults) {
			ret.put(prop.name.toLowerCase(), prop);
		}
		return ret;
	}

}
