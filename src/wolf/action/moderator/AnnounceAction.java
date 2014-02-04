package wolf.action.moderator;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import wolf.WolfException;
import wolf.action.Action;
import wolf.action.Visibility;
import wolf.model.Player;
import wolf.model.stage.GameStage;

public class AnnounceAction extends Action {

  public AnnounceAction(GameStage stage) {
    super(stage, "announce", "message");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    StringBuilder output = new StringBuilder();
    for (String s : args) {
      output.append(s).append(" ");
    }
    output.setLength(output.length() - 1);
    getBot().sendMessage("ANNOUNCEMENT - " + output.toString());
  }

  @Override
  public void apply(Player invoker, List<String> args, boolean isPrivate) {
    checkNotNull(invoker);
    checkNotNull(args);

    if (requiresAdmin() && !invoker.isAdmin()) {
      throw new WolfException("You must be an admin to do that.");
    }

    execute(invoker, args);
  }

  @Override
  public String getDescription() {
    return "Have the bot send an announcement to the channel. (admin only)";
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

  @Override
  public Visibility getVisibility() {
    return Visibility.PRIVATE;
  }

}
