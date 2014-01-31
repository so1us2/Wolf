package wolf.action;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import wolf.WolfException;
import wolf.model.GameModel;
import wolf.model.Player;

public abstract class Action {

  private final String name;
  private final int numArgs;

  public Action(String name, int numArgs) {
    this.name = name;
    this.numArgs = numArgs;
  }

  public void apply(GameModel model, Player invoker, List<String> args) {
    checkNotNull(invoker);
    checkNotNull(args);

    if (args.size() != numArgs) {
      throw new WolfException("Expected " + numArgs + " arguments.");
    }

    if (requiresAdmin() && !invoker.isAdmin()) {
      throw new WolfException("You must be an admin to do that.");
    }

    execute(model, invoker, args);
  }

  protected abstract void execute(GameModel model, Player invoker, List<String> args);

  public String getName() {
    return name;
  }

  public int getNumArgs() {
    return numArgs;
  }

  protected boolean requiresAdmin() {
    // subclasses can override
    return false;
  }

  protected boolean isPrivateAction() {
    // subclasses can override
    return false;
  }

}
