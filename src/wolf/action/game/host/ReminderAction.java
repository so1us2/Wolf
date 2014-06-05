package wolf.action.game.host;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import wolf.action.game.GameAction;
import wolf.model.Player;
import wolf.model.stage.GameStage;


public class ReminderAction extends GameAction {

  public ReminderAction(GameStage stage) {
    super(stage, "remind");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    if (getStage().isDay()) {
      Set<Player> nonvoters =
          Sets.difference(getStage().getPlayers(), getStage().getVotesToDayKill().keySet());
      for (Player p : nonvoters) {
        getBot().sendMessage(p.getName(), "You have not voted today yet.");
      }
    } else if (getStage().isNight()) {
      for (Player p : getStage().getPlayers()) {
        if (!p.getRole().isFinishedWithNightAction()) {
          getBot().sendMessage(p.getName(),
              "Reminder: please take your night action. The game is waiting on you.");
        }
      }
    }
    getBot().sendMessage(invoker.getName(), "Reminder sent.");
  }

  @Override
  public String getDescription() {
    return "Remind players who need to act to do so.";
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

  @Override
  protected boolean onlyIfAlive() {
    return false;
  }

}
