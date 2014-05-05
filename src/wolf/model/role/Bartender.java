package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Bartender extends AbstractRole {

  private Player drinkTarget;
  private boolean hasActed = false;
  private List<Player> drinkHistory = Lists.newArrayList();

  @Override
  public void onNightBegins() {
    drinkTarget = null;
    hasActed = false;
    getBot()
        .sendMessage(getPlayer().getName(),
            "Who do you want to send a drink?  Message me /drink <target> or /pass to not serve anyone.");
  }

  @Override
  public void onNightEnds() {
    if (getStage().isCorrupterTarget(getPlayer())) {
      return;
    }
    if (drinkTarget != null) {
      getBot().sendMessage(drinkTarget.getName() + " has a drink waiting for them.");
    }
    drinkHistory.add(drinkTarget);
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return hasActed;
  }

  @Override
  public List<Action> getNightActions() {
    return ImmutableList.<Action>of(drinkAction, passAction);
  }

  @Override
  public Player getSpecialTarget() {
    return drinkTarget;
  }

  @Override
  public String getDescription() {
    return "The Bartender can give drinks to players at night and it is announced to everyone when someone receives a drink.";
  }

  private Action drinkAction = new Action("drink", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Bartender.this.getStage();

      hasActed = true;
      drinkTarget = stage.getPlayer(args.get(0));
      stage.getBot().sendMessage(invoker.getName(),
          "You plan to make a drink for " + drinkTarget + " ;)");
    }

    @Override
    public String getDescription() {
      return "You send a drink to someone.";
    }
  };

  private Action passAction = new Action("pass") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Bartender.this.getStage();

      hasActed = true;
      drinkTarget = null;
      stage.getBot().sendMessage(invoker.getName(), "You decide not to make a drink for anyone.");
    }

    @Override
    public String getDescription() {
      return "You don't make a drink for anyone tonight.";
    }
  };


}
