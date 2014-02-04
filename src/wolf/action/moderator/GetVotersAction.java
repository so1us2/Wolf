package wolf.action.moderator;

import java.util.List;
import java.util.Map;

import org.testng.collections.Lists;

import wolf.action.Visibility;
import wolf.action.game.GameAction;
import wolf.model.Player;
import wolf.model.stage.GameStage;

public class GetVotersAction extends GameAction {


  public GetVotersAction(GameStage stage) {
    super(stage, "voters");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {

    Map<Player, Player> votes = getStage().getVotesToDayKill();
    List<Player> nonvoters = Lists.newArrayList();

    for (Player p : getStage().getPlayers()) {
      if (!votes.keySet().contains(p)) {
        nonvoters.add(p);
      }
    }

    StringBuilder output = new StringBuilder();
    output.append("Voters: ");

    for (Player p : votes.keySet()) {
      output.append(p.getName() + ", ");
    }
    output.setLength(output.length() - 2);
    getBot().sendMessage(invoker.getName(), output.toString());

    output = new StringBuilder();
    output.append("Non-voters: ");
    for (Player p : nonvoters) {
      output.append(p.getName() + ", ");
    }
    output.setLength(output.length() - 2);
    getBot().sendMessage(invoker.getName(), output.toString());
  }

  @Override
  public String getDescription() {
    return "List voting status of each player. (admin only)";
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
