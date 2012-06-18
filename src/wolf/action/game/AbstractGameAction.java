package wolf.action.game;

import wolf.action.BotAction;
import wolf.engine.WolfEngine;

public abstract class AbstractGameAction extends BotAction {

	protected WolfEngine engine;

	public AbstractGameAction() {
		super();
	}

	public AbstractGameAction(int numArgs) {
		super(numArgs);
	}

	public void setHandler(WolfEngine engine) {
		this.engine = engine;
	}

}
