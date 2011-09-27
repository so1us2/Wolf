package wolf.role;

import wolf.arch.DisplayName;

public abstract class GameRole {

	@Override
	public String toString() {
		DisplayName displayName = getClass().getAnnotation(DisplayName.class);
		if (displayName == null) {
			return getClass().getSimpleName();
		}
		return displayName.value();
	}

}
