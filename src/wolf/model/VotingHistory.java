package wolf.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import wolf.bot.IBot;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class VotingHistory {

  private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("hh:mm ss");

  private final List<List<Vote>> history = Lists.newArrayList();

  public VotingHistory() {
    nextRound();
  }

  public void record(Player player, Player voteTarget) {
    List<Vote> list = Iterables.getLast(history);
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
      bot.sendMessage("Voting Round " + roundNumber);
      printRound(bot, roundNumber);
      bot.sendMessage("");
    }
  }

  public void printRound(IBot bot, int roundNumber) {
    List<Vote> round = history.get(roundNumber - 1);
    bot.sendMessage("Round " + roundNumber);
    for (Vote vote : round) {
      bot.sendMessage(format.format(vote.getTimestamp()) + ": " + vote.getPlayer().getName()
          + " -> " + vote.getTarget());
    }
  }

  public void reset() {
    history.clear();
    nextRound();
  }

  private static class Vote {
    private final LocalDateTime timestamp = LocalDateTime.now();
    private final Player player;
    private final Player target;

    public Vote(Player player, Player target) {
      this.player = player;
      this.target = target;
    }

    public LocalDateTime getTimestamp() {
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
