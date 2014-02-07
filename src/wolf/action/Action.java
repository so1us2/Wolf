package wolf.action;

import java.util.List;

import com.google.common.collect.ImmutableList;
import wolf.WolfException;
import wolf.bot.IBot;
import wolf.model.Player;
import wolf.model.stage.Stage;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class Action implements Comparable<Action> {

  private final Stage stage;
  private final String name;
  private final List<String> argNames;

  public Action(String name, String... argsNames) {
    this(null, name, argsNames);
  }

  public Action(Stage stage, String name, String... argsNames) {
    this.stage = stage;
    this.name = name;
    this.argNames = ImmutableList.copyOf(argsNames);
  }

  public void apply(Player invoker, List<String> args) {
    checkNotNull(invoker);
    checkNotNull(args);

    if (args.size() != argNames.size()) {
      throw new WolfException("Expected " + argNames.size() + " arguments.");
    }

    if (requiresAdmin() && !invoker.isAdmin()) {
      throw new WolfException("You must be an admin to do that.");
    }

    if (!invoker.isAlive()) {
      throw new WolfException("You cannot do anything while dead.");
    }

    execute(invoker, args);
  }

  protected abstract void execute(Player invoker, List<String> args);
  
  public abstract String getDescription();

  public String getUsage() {
    StringBuilder sb = new StringBuilder();

    sb.append("/").append(getName());

    for (String arg : argNames) {
      sb.append(" <").append(arg).append(">");
    }

    return sb.toString();
  }

  public String getName() {
    return name;
  }

  public int getNumArgs() {
    return argNames.size();
  }

  protected boolean requiresAdmin() {
    // subclasses can override
    return false;
  }

  public IBot getBot() {
    return stage.getBot();
  }

  public Stage getStage() {
    return stage;
  }

  @Override
  public int compareTo(Action o) {
    return name.compareTo(o.getName());
  }

}
