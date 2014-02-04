package wolf.action.game;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.WolfException;
import wolf.action.Visibility;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

public class VoteAction extends GameAction {

  public VoteAction(GameStage stage) {
    super(stage, "vote", "player");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Player target = getStage().getPlayer(args.get(0));
    Map<Player, Player> votes = getStage().getVotesToDayKill();

    Player prevTarget = votes.put(invoker, target);

    if (Objects.equal(target, prevTarget)) {
      throw new WolfException("Your vote was already set to " + target.getName());
    }

    getStage().getVotingHistory().record(invoker, target);

    if (prevTarget == null) {
      getBot().sendMessage(invoker.getName(), "Voted for " + target.getName());
      getBot().sendMessage("A player voted. (" + votes.size() + " total)");
    } else {
      getBot().sendMessage(invoker.getName(), "Switched vote to " + target.getName());
      getBot().sendMessage("A player switched their vote. (" + votes.size() + " total)");
    }

    if (votes.size() == getStage().getPlayers().size()) {
      processVotes(votes);
    }
  }

  /**
   * Checks for a majority -- and if there is one, performs a villager kill.
   */
  private void processVotes(Map<Player, Player> votes) {
    Player dayKillTarget = getMajorityVote(votes);

    if (dayKillTarget == null) {
      votes.clear();
      getStage().getVotingHistory().nextRound();
      getBot().sendMessage("No majority was reached.");
    } else {
      dayKillTarget.setAlive(false);
      getStage().getVotingHistory().print(getBot());
      getStage().getVotingHistory().reset();
      getStage().getVotesToDayKill().clear();
      getBot().sendMessage("A verdict was reached and " + dayKillTarget.getName() + " was killed.");
      getBot().sendMessage(
          dayKillTarget.getName() + " was a "
              + dayKillTarget.getRole().getFaction().getSingularForm());
      if (getStage().checkForWinner() != null) {
        // game is over, don't need to do any more logic here.
        return;
      }
      getStage().moveToNight();
    }
  }

  private Player getMajorityVote(Map<Player, Player> votes) {
    Map<Player, Integer> voteTally = Maps.newLinkedHashMap();

    for (Player target : votes.values()) {
      Integer i = voteTally.get(target);
      if (i == null) {
        i = 0;
      }
      voteTally.put(target, i + 1);
    }

    int votesNeededToWin;
    int numPlayers = getStage().getPlayers().size();

    if (numPlayers % 2 == 0) {
      votesNeededToWin = numPlayers / 2 + 1;
    } else {
      votesNeededToWin = (int) Math.ceil(numPlayers / 2.0);
    }

    for (Entry<Player, Integer> e : voteTally.entrySet()) {
      if (e.getValue() >= votesNeededToWin) {
        return e.getKey();
      }
    }

    return null;
  }

  @Override
  public String getDescription() {
    return "Votes to have a player lynched.";
  }

  @Override
  public Visibility getVisibility() {
    return Visibility.PRIVATE;
  }

}
