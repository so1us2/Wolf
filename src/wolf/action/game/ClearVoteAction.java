package wolf.action.game;

import java.util.List;
import java.util.Map;

import wolf.model.Player;
import wolf.model.stage.GameStage;

public class ClearVoteAction extends GameAction {

  public ClearVoteAction(GameStage stage) {
    super(stage, "clear");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Map<Player, Player> votes = getStage().getVotesToDayKill();
    if (votes.get(invoker) == null) {
      getStage().getBot().sendMessage(invoker.getName(), "You have not voted yet.");
      return;
    }

    votes.remove(invoker);
    getStage().getVotingHistory().record(invoker, null);
    getBot().sendMessage(invoker.getName(), "Your vote has been cleared.");
    if (getStage().getSetting("ANNOUNCE_VOTES").equals("YES")) {
      printVotes();
    } else {
      getBot().sendMessage("A player cleared their vote. (" + votes.size() + " total votes)");
    }
  }

  private void printVotes() {
    Map<Player, Player> votes = getStage().getVotesToDayKill();
    StringBuilder sb = new StringBuilder();
    getBot().sendMessage("VOTES");
    for (Player p : votes.keySet()) {
      sb.append(p).append("-->").append(votes.get(p)).append(", ");
    }
    sb.setLength(sb.length() - 2);
    getBot().sendMessage(sb.toString());
  }

  @Override
  public String getDescription() {
    return "Clear your current vote so you are voting for no one.";
  }

}
