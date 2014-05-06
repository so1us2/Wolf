package wolf.action.game.host;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.base.Joiner;

public class AnnounceAction extends Action {

  public AnnounceAction(GameStage stage) {
    super(stage, "announce", "message");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    StringBuilder output = new StringBuilder();
    output.append("ANNOUNCEMENT - ").append("(").append(invoker.getName()).append(") ")
        .append(Joiner.on(" ").join(args));
    getBot().sendMessage(output.toString());
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

  @Override
  protected boolean argSizeMatters() {
    return false;
  }

  @Override
  protected boolean onlyIfAlive() {
    return false;
  }

  @Override
  public String getDescription() {
    return "Makes an announcement to the channel.";
  }

}
