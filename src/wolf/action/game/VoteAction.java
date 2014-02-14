package wolf.action.game;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.WolfException;
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
    // else if (Objects.equal(target, invoker)) {
    // throw new WolfException("You cannot vote for yourself.");
    // }

    getStage().getVotingHistory().record(invoker, target);

    if (prevTarget == null) {
      getBot().sendMessage(invoker.getName(), "Voted for " + target.getName());
      getBot().sendMessage("A player voted. (" + votes.size() + " total)");
      getBot().onPlayersChanged();
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
    Map<Player, Integer> tally = tallyVotes(votes);
    Player dayKillTarget = getMajorityVote(tally);

    if (dayKillTarget == null) {
      String mode = getStage().getSetting("ANNOUNCE_ON_TIE");
      if (mode.equals("NONE")) {
        getBot().sendMessage("No majority was reached.");
      } else if (mode.equals("TOTALS")) {
        for (Player p : tally.keySet()) {
          getBot().sendMessage(p.getName() + " (" + tally.get(p) + ")");
        }
      } else if (mode.equals("ALL")) {
        getStage().getVotingHistory().printRound(getBot(),
            getStage().getVotingHistory().getCurrentRound());
      }
      votes.clear();
      getStage().getVotingHistory().nextRound();
    } else {
      dayKillTarget.setAlive(false);
      getStage().getVotingHistory().print(getBot());
      getStage().getVotingHistory().reset();
      getStage().getVotesToDayKill().clear();
      getBot().sendMessage("A verdict was reached and " + dayKillTarget.getName() + " was killed.");
      String mode = getStage().getSetting("DAY_KILL_ANNOUNCE");
      if (mode.equals("FACTION")) {
        getBot().sendMessage(
            dayKillTarget.getName() + " was a "
                + dayKillTarget.getRole().getFaction().getSingularForm());
      } else if (mode.equals("ROLE")) {
        getBot().sendMessage(
            dayKillTarget.getName() + " was a " + dayKillTarget.getRole().getType());
      } else if (mode.equals("SILENT")) {}
      getBot().onPlayersChanged();
      if (getStage().checkForWinner() != null) {
        // game is over, don't need to do any more logic here.
        return;
      }
      getStage().moveToNight();
    }
  }

  private Map<Player, Integer> tallyVotes(Map<Player, Player> votes) {
    Map<Player, Integer> voteTally = Maps.newLinkedHashMap();

    for (Player target : votes.values()) {
      Integer i = voteTally.get(target);
      if (i == null) {
        i = 0;
      }
      voteTally.put(target, i + 1);
    }

    return voteTally;
  }

  private Player getMajorityVote(Map<Player, Integer> tally) {

    int votesNeededToWin;
    int numPlayers = getStage().getPlayers().size();

    if (numPlayers % 2 == 0) {
      votesNeededToWin = numPlayers / 2 + 1;
    } else {
      votesNeededToWin = (int) Math.ceil(numPlayers / 2.0);
    }

    for (Entry<Player, Integer> e : tally.entrySet()) {
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

}
