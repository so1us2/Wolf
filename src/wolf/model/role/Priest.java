package wolf.model.role;

import java.util.List;

import wolf.WolfException;
import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;

public class Priest extends AbstractRole {

  private Player lastProtectedTarget;
  private Player protectTarget;

  @Override
  public void onNightBegins() {
    protectTarget = null;

    getBot().sendMessage(getPlayer().getName(),
        "Who do you want to protect?  Message me !protect <target>");
  }

  @Override
  public void onNightEnds() {
    lastProtectedTarget = protectTarget;
    protectTarget = null;
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return protectTarget != null;
  }

  @Override
  public List<Action> getNightActions() {
    return ImmutableList.<Action>of(protectAction);
  }

  @Override
  public Player getTarget() {
    return protectTarget;
  }

  @Override
  public String getDescription() {
    return "The Priest can protect a player each night, preventing that player from being killed.";
  }

  private Action protectAction = new Action("protect", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Priest.this.getStage();

      protectTarget = stage.getPlayer(args.get(0));

      if (Objects.equal(protectTarget, invoker)) {
        // make sure we are allowed to protect ourselves
        if (!stage.getSetting("SELF_PROTECT").equals("YES")) {
          throw new WolfException("You are not allowed to protect yourself.");
        }
      }

      if (protectTarget == lastProtectedTarget) {
        stage.getBot().sendMessage(invoker.getName(),
            "You cannot protect " + protectTarget + " twice in a row.");
        protectTarget = null;
        return;
      }

      stage.getBot().sendMessage(invoker.getName(),
          "Your wish to protect " + protectTarget + " has been received.");
    }

    @Override
    public String getDescription() {
      return "Protects the target from being killed by wolves tonight.";
    }
  };

}
