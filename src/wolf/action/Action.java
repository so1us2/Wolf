package wolf.action;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import wolf.WolfException;
import wolf.bot.NarratorBot;
import wolf.model.Player;
import wolf.model.Stage;

public abstract class Action {

  private final Stage stage;
  private final String name;
  private final int numArgs;

  public Action(Stage stage, String name, int numArgs) {
    this.stage = stage;
    this.name = name;
    this.numArgs = numArgs;
  }

  public void apply(Player invoker, List<String> args) {
    checkNotNull(invoker);
    checkNotNull(args);

    if (args.size() != numArgs) {
      throw new WolfException("Expected " + numArgs + " arguments.");
    }

    if (requiresAdmin() && !invoker.isAdmin()) {
      throw new WolfException("You must be an admin to do that.");
    }

    execute(invoker, args);
  }

  protected abstract void execute(Player invoker, List<String> args);
  
  public abstract String getDescription();

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

  public boolean isPrivateAction() {
    // subclasses can override
    return false;
  }

  public NarratorBot getBot() {
    return stage.getBot();
  }

}
