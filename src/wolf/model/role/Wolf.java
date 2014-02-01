package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.action.Visibility;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;

public class Wolf extends AbstractRole {

  private Player killTarget;

  @Override
  public void onNightBegins() {
    killTarget = null;
    getBot().sendMessage(getPlayer().getName(),
        "Who do you want to kill?  Message me !kill <target>");
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return killTarget != null;
  }

  @Override
  public List<Action> getNightActions() {
    return ImmutableList.<Action>of(killAction);
  }
  
  public Player getKillTarget() {
    return killTarget;
  }

  @Override
  public void handleChat(Player sender, String message, boolean isPrivate) {
    if (getStage().isNight() && isPrivate) {
      // wolf-chat
      for (Player wolf : getStage().getPlayers(Role.WOLF)) {
        if (wolf != sender) {
          getBot().sendMessage(wolf.getName(), "<WolfChat> " + sender + ": " + message);
        }
      }
    }
  }

  private Action killAction = new Action("kill", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Wolf.this.getStage();

      killTarget = stage.getPlayer(args.get(0));
      stage.getBot().sendMessage(invoker.getName(),
          "Your wish to kill " + killTarget + " has been received.");
    }

    @Override
    public String getDescription() {
      return "Feast on their flesh! The target will not awaken in the morning...";
    }

    @Override
    public Visibility getVisibility() {
      return Visibility.PRIVATE;
    };
  };

}
