package wolf.action.init;

import wolf.GameInitializer;
import wolf.action.BotAction;

public abstract class AbstractInitAction extends BotAction {

  protected GameInitializer initializer;

  public AbstractInitAction() {
    super();
  }

  public AbstractInitAction(int numArgs) {
    super(numArgs);
  }

  public void setInitializer(GameInitializer initializer) {
    this.initializer = initializer;
  }

}
