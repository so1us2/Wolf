package wolf.action.moderator;

import java.util.List;
import java.util.Map;

import wolf.action.game.GameAction;
import wolf.model.Player;
import wolf.model.stage.GameStage;

public class GetVotersAction extends GameAction {


  public GetVotersAction(GameStage stage, String name, String[] argNames) {
    super(stage, "voters");
    // TODO Auto-generated constructor stub
  }

  @Override
  protected void execute(Player invoker, List<String> args) {

    Map<Player, Player> votes = getStage().getVotesToDayKill();


    for (Player p : getStage().getPlayers())
    {

    }
  }

  @Override
  public String getDescription() {
    return "List which players have not voted.";
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

}
