package wolf.action.game;

import java.util.List;

import wolf.model.GameStage;
import wolf.model.Player;

public class VoteAction extends GameAction {

  public VoteAction(GameStage stage) {
    super(stage, "vote", "player");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getStage().getVotesToLynch().put(invoker, getStage().getPlayer(args.get(0)));

    // TODO send messages & check to see if there is a majority
  }

  @Override
  public String getDescription() {
    return "Votes to have a player lynched.";
  }

}
