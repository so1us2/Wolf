package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.action.Visibility;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;

public class Seer extends AbstractRole {

  private Player peekTarget;

  @Override
  public void onNightBegins() {
    peekTarget = null;

    getBot().sendMessage(getPlayer().getName(),
        "Who do you want to peek?  Message me !peek <target>");
  }
  
  @Override
  public boolean isFinishedWithNightAction() {
    return peekTarget != null;
  }

  @Override
  public List<Action> getNightActions() {
    return ImmutableList.<Action>of(peekAction);
  }
  
  private Action peekAction = new Action("peek", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Seer.this.getStage();

      if (peekTarget != null) {
        stage.getBot().sendMessage(invoker.getName(), "You've can't peek twice in one night!");
        return;
      }

      peekTarget = stage.getPlayer(args.get(0));

      if (peekTarget.getRole().getFaction() == Faction.WOLVES) {
        stage.getBot().sendMessage(invoker.getName(),
            "RAWRRRR!! " + peekTarget.getName() + " is a wolf.");
      } else {
        stage.getBot().sendMessage(invoker.getName(), peekTarget.getName() + " is a villager.");
      }
    }

    @Override
    public String getDescription() {
      return "Gain information on whether that person is one of the villagers or wolves.";
    }

    @Override
    public Visibility getVisibility() {
      return Visibility.PRIVATE;
    };
  };

}
