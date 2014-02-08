package wolf.model;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import wolf.bot.IBot;

import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

public class GameSummary {

  public static void printGameLog(IBot bot, Collection<Player> players, Faction winner,
      List<Multimap<Player, Player>> killHistory) {
    Set<Player> winners = Sets.newTreeSet();
    Set<Player> losers = Sets.newTreeSet();

    for (Player player : players) {
      Faction faction = player.getRole().getVictoryTeamFaction();
      if (winner == faction) {
        winners.add(player);
        player.markWinner();
      } else {
        losers.add(player);
      }
    }

    StringBuilder output = new StringBuilder();
    output.append("Winners: ");
    for (Player p : winners) {
      output.append(p.getName()).append(" (").append(p.getRole()).append(")");
      if (p.isAlive()) {
        output.append(" *ALIVE*");
      }
      output.append(", ");
    }
    output.setLength(output.length() - 2);
    bot.sendMessage(output.toString());

    bot.sendMessage("");
    output = new StringBuilder();
    output.append("Losers: ");
    for (Player p : losers) {
      output.append(p.getName()).append(" (").append(p.getRole()).append(")");
      if (p.isAlive()) {
        output.append(" *ALIVE*");
      }
      output.append(", ");
    }
    output.setLength(output.length() - 2);
    bot.sendMessage(output.toString());
    
    bot.sendMessage("GAME REPLAY");
    int count = 1;
    for (Multimap<Player, Player> day : killHistory) {
      bot.sendMessage("Night " + count++);
      if (day.keySet().isEmpty()) {
        bot.sendMessage("No deaths.");
      }
      for (Player victim : day.keySet()) {
        output = new StringBuilder();
        output.append(victim.getName()).append(" (").append(victim.getRole()).append(")")
            .append(" was killed by ");
        for (Player killer : day.get(victim)) {
          output.append(killer.getName()).append(" (").append(killer.getRole()).append(")")
              .append(" and ");
        }
        output.setLength(output.length() - 5);
        bot.sendMessage(output.toString());
      }
    }
  }

}
