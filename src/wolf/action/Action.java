package wolf.action;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import wolf.WolfException;
import wolf.bot.IBot;
import wolf.model.Player;
import wolf.model.stage.Stage;

import com.google.common.collect.ImmutableList;

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

    if (argSizeMatters() && (args.size() != argNames.size())) {
      throw new WolfException("Expected " + argNames.size() + " arguments.");
    }

    if (requiresAdmin() && !invoker.isAdmin()) {
      throw new WolfException("You must be an admin to do that.");
    }

    if (onlyIfAlive() && !invoker.isAlive()) {
      throw new WolfException("You cannot do anything while dead.");
    }
    
    if (requiresHost() && !(invoker.equals(stage.getHost()) || invoker.isAdmin())) {
      getStage().getBot().sendMessage("Invoker is admin = " + invoker.isAdmin());
      throw new WolfException("You must be the game host to do that.");
    }

    execute(invoker, args);
  }

  protected boolean requiresHost() {
    return false;
  }

  protected boolean requiresAdmin() {
    return false;
  }

  protected boolean onlyIfAlive() {
    return true;
  }

  protected boolean argSizeMatters() {
    return true;
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
