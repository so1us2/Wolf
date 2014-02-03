package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.action.Visibility;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Seer extends AbstractRole {

  private Player peekTarget;
  private List<Player> peekHistory = Lists.newArrayList();

  @Override
  public void onNightBegins() {
    peekTarget = null;

    getBot().sendMessage(getPlayer().getName(),
        "Who do you want to peek?  Message me !peek <target>");
  }
  
  @Override
  public void onNightEnds(Player player) {
    peekHistory.add(peekTarget);

    if (peekTarget.getRole().getFaction() == Faction.WOLVES) {
      getStage().getBot().sendMessage(player.getName(),
          "RAWRRRR!! " + peekTarget.getName() + " is a wolf.");
    } else {
      getStage().getBot().sendMessage(player.getName(), peekTarget.getName() + " is a villager.");
    }
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

      peekTarget = stage.getPlayer(args.get(0));

      getStage().getBot().sendMessage(invoker.getName(), "You focus your reading on " + peekTarget);
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
