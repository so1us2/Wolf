package wolf.model;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import wolf.bot.IBot;

import com.google.common.collect.Lists;

public class VotingHistory {

  private static final DateTimeFormatter format = DateTimeFormat.forPattern("hh:mm ss");

  private final List<List<Vote>> history = Lists.newArrayList();

  public VotingHistory() {
    nextRound();
  }

  public void record(Player player, Player voteTarget) {
    List<Vote> list = history.get(history.size() - 1);
    list.add(new Vote(player, voteTarget));
  }

  public void nextRound() {
    history.add(Lists.<Vote>newArrayList());
  }

  public int getCurrentRound() {
    return history.size();
  }

  public void print(IBot bot) {
    for (int roundNumber = 1; roundNumber <= history.size(); roundNumber++) {
      printRound(bot, roundNumber);
    }
  }

  public void printRound(IBot bot, int roundNumber) {
    List<Vote> round = history.get(roundNumber - 1);
    bot.sendMessage("Round " + roundNumber);
    for (Vote vote : round) {
      bot.sendMessage(format.print(vote.getTimestamp()) + ": " + vote.getPlayer().getName()
          + " -> " + vote.getTarget());
    }
  }

  public void reset() {
    history.clear();
    nextRound();
  }

  private static class Vote {
    private final DateTime timestamp = new DateTime();
    private final Player player;
    private final Player target;

    public Vote(Player player, Player target) {
      this.player = player;
      this.target = target;
    }

    public DateTime getTimestamp() {
      return timestamp;
    }

    public Player getPlayer() {
      return player;
    }

    public Player getTarget() {
      return target;
    }
  }

}
