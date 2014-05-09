package wolf.action.game;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.WolfException;
import wolf.bot.IBot;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import static com.google.common.collect.Iterables.getOnlyElement;

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
      if (getStage().getSetting("ANNOUNCE_VOTES").equals("YES")) {
        getStage().printVotes();
      } else {
        getBot().sendMessage("A player voted. (" + votes.size() + " total)");
      }
      getBot().onPlayersChanged();
    } else {
      getBot().sendMessage(invoker.getName(), "Switched vote to " + target.getName());
      if (getStage().getSetting("ANNOUNCE_VOTES").equals("YES")) {
        getStage().printVotes();
      } else {
        getBot().sendMessage("A player switched their vote. (" + votes.size() + " total)");
      }
    }
    processVotes(getBot(), getStage(), votes, false);
  }

  public void printVotes() {
    Map<Player, Player> votes = getStage().getVotesToDayKill();
    StringBuilder sb = new StringBuilder();
    getBot().sendMessage("VOTES");
    for (Player p : votes.keySet()) {
      sb.append(p).append("-->").append(votes.get(p)).append(", ");
    }
    sb.setLength(sb.length() - 2);
    getBot().sendMessage(sb.toString());
  }

  /**
   * Checks for a majority -- and if there is one, performs a villager kill.
   * 
   * @param forceKill Used by the Timer System to force a kill even with no majority.
   */
  public static void processVotes(IBot bot, GameStage stage, Map<Player, Player> votes,
      boolean forceKill) {
    Map<Player, Integer> tally = tallyVotes(votes);
    Player dayKillTarget = null;

    // figure out if someone is dying
    if (stage.getSetting("VOTING_METHOD").equals("END_ON_MAJORITY") || forceKill) {
      dayKillTarget = getMajorityVote(tally, forceKill, stage);
    } else if (stage.getSetting("VOTING_METHOD").equals("ALL_VOTES")) {
      if (votes.size() == stage.getPlayers().size()) {
        dayKillTarget = getMajorityVote(tally, forceKill, stage);
      }
    }

    // if everyone has voted and there is no majority, clear votes.
    if (votes.size() == stage.getPlayers().size() && dayKillTarget == null) {
      String mode = stage.getSetting("ANNOUNCE_ON_TIE");
      if (mode.equals("NONE")) {
        bot.sendMessage("No majority was reached.");
        return;
      } else if (mode.equals("TOTALS")) {
        for (Player p : tally.keySet()) {
          bot.sendMessage(p.getName() + " (" + tally.get(p) + ")");
        }
      } else if (mode.equals("ALL")) {
        stage.getVotingHistory().printRound(bot,
            stage.getVotingHistory().getCurrentRound());
      }
      votes.clear();
      stage.getVotingHistory().nextRound();
      return;
    }

    // resolve voting
    if (dayKillTarget == null) {
      return;
    } else {
      dayKillTarget.setAlive(false);
      stage.getVotingHistory().print(bot);
      stage.getVotingHistory().reset();
      stage.getVotesToDayKill().clear();
      bot.sendMessage("A verdict was reached and " + dayKillTarget.getName() + " was killed.");
      String mode = stage.getSetting("DAY_KILL_ANNOUNCE");
      if (mode.equals("FACTION")) {
        bot.sendMessage(
            dayKillTarget.getName() + " was a "
                + dayKillTarget.getRole().getFaction().getSingularForm());
      } else if (mode.equals("ROLE")) {
        bot.sendMessage(
            dayKillTarget.getName() + " was a " + dayKillTarget.getRole().getType());
      } else if (mode.equals("SILENT")) {}
      bot.onPlayersChanged();
      if (stage.checkForWinner() != null) {
        // game is over, don't need to do any more logic here.
        return;
      }
      stage.moveToNight();
    }
  }

  private static Map<Player, Integer> tallyVotes(Map<Player, Player> votes) {
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

  /**
   * If 'forcekill' is true, this method will never return null.
   */
  private static Player getMajorityVote(Map<Player, Integer> tally, boolean forceKill,
      GameStage stage) {
    int numPlayers = stage.getPlayers().size();

    int votesNeededToWin;
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

    if (forceKill) {
      return getForceKill(tally, stage);
    }

    return null;
  }

  private static Player getForceKill(Map<Player, Integer> tally,
      GameStage stage) {
    int maxTally = 0;
    for (Entry<Player, Integer> e : tally.entrySet()) {
      maxTally = Math.max(maxTally, e.getValue());
    }

    List<Player> possibleTargets = Lists.newArrayList();
    if (maxTally == 0) {
      // return a random player because no-one has voted.
      possibleTargets.addAll(stage.getPlayers());
    } else {
      for (Entry<Player, Integer> e : tally.entrySet()) {
        if (e.getValue() == maxTally) {
          possibleTargets.add(e.getKey());
        }
      }
    }

    if (possibleTargets.isEmpty()) {
      // this should never happen
      System.err.println("Tried to force kill but no players left alive??");
      return null;
    } else if (possibleTargets.size() == 1) {
      return getOnlyElement(possibleTargets);
    } else {
      // Multiple people are tied. Choose one randomly.
      return possibleTargets.get((int) (Math.random() * possibleTargets.size()));
    }
  }

  @Override
  public String getDescription() {
    return "Votes to have a player lynched.";
  }

}
