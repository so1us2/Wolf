package wolf.action.moderator;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;
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
    Set<Player> nonvoters = Sets.difference(getStage().getPlayers(), votes.keySet());

    StringBuilder output = new StringBuilder();
    output.append("Voters: ");
    output.append(Joiner.on(", ").join(votes.keySet()));
    getBot().sendMessage(invoker.getName(), output.toString());

    output = new StringBuilder();
    output.append("Non-voters: ");
    output.append(Joiner.on(", ").join(nonvoters));
    getBot().sendMessage(invoker.getName(), output.toString());
  }

  @Override
  public String getDescription() {
    return "List voting status of each player.";
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

}
