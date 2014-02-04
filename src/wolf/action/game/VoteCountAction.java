package wolf.action.game;

import java.util.List;

import wolf.model.stage.GameStage;

import wolf.model.Player;

public class VoteCountAction extends GameAction {

  public VoteCountAction(GameStage stage) {
    super(stage, "votes");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getBot().sendMessage(
        getStage().getVotesToDayKill().size() + " of " + getStage().getPlayers().size()
            + " players have voted.");
  }

  @Override
  public String getDescription() {
    return "Tells you how many players have voted.";
  }

}
