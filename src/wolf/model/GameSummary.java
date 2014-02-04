package wolf.model;

import java.util.Collection;
import java.util.Set;

import wolf.bot.IBot;

import com.google.common.collect.Sets;

public class GameSummary {

  public static void printGameLog(IBot bot, Collection<Player> players, Faction winner) {
    Set<Player> winners = Sets.newTreeSet();
    Set<Player> losers = Sets.newTreeSet();

    for (Player player : players) {
      Faction faction = player.getRole().getVictoryTeamFaction();
      if (winner == faction) {
        winners.add(player);
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
  }

}
