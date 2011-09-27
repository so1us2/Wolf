package wolf.action.init;

import wolf.GameInitializer;
import wolf.action.BotAction;

public abstract class AbstractInitAction extends BotAction {

	protected GameInitializer initializer;

	public void setInitializer(GameInitializer initializer) {
		this.initializer = initializer;
	}

}
