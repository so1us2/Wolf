package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.action.Visibility;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;

public class Vigilante extends AbstractRole {

  boolean hasFired = false;
  boolean hasActed = false;
  private Player killTarget;

  @Override
  public void onNightBegins() {
    if (!hasFired) {
    killTarget = null;
    getBot().sendMessage(getPlayer().getName(),
          "Who do you want to shoot?  Message me !shoot <target>");
    }
  }

  @Override
  public void onNightEnds(Player player) {
    if (killTarget != null) {
      hasFired = true;
      killTarget = null;
    }

    hasActed = false;
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return hasActed || hasFired;
  }

  @Override
  public List<Action> getNightActions() {
    if (hasFired) {
      return ImmutableList.of();
    } else {
      return ImmutableList.<Action>of(shootAction, passAction);
    }
  }

  public Player getKillTarget() {
    return killTarget;
  }

  private Action shootAction = new Action("shoot", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Vigilante.this.getStage();

      hasActed = true;
      killTarget = stage.getPlayer(args.get(0));
      stage.getBot().sendMessage(invoker.getName(), "You aim at " + killTarget + ".");
    }

    @Override
    public String getDescription() {
      return "You have a single bullet that you can shoot.";
    }

    @Override
    public Visibility getVisibility() {
      return Visibility.PRIVATE;
    };
  };

  private Action passAction = new Action("pass") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Vigilante.this.getStage();

      hasActed = true;
      killTarget = null;
      stage.getBot().sendMessage(invoker.getName(), "You holster your pistol.");
    }

    @Override
    public String getDescription() {
      return "You hold your fire and do not kill anyone.";
    }

    @Override
    public Visibility getVisibility() {
      return Visibility.PRIVATE;
    };
  };


}
