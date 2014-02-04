package wolf.action.moderator;

import java.util.List;

import wolf.action.Visibility;
import wolf.action.game.GameAction;
import wolf.model.Player;
import wolf.model.stage.GameStage;


public class ReminderAction extends GameAction {

  public ReminderAction(GameStage stage) {
    super(stage, "remind");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    for (Player p : getStage().getPlayers()) {
      if (!p.getRole().isFinishedWithNightAction()) {
        getBot().sendMessage(p.getName(),
            "Reminder: please take your night action. The game is waiting on you.");
      }
    }
  }

  @Override
  public String getDescription() {
    return "Remind players who need to act to do so. (admin only)";
  }

  @Override
  public Visibility getVisibility() {
    return Visibility.BOTH;
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

}
