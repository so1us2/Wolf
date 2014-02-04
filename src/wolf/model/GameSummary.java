package wolf.model;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;
import wolf.bot.IBot;

public class GameSummary {

  public static void printGameLog(IBot bot, Collection<Player> players, Faction winner) {
    // TODO: Sort the outputs by alive/dead and then alphabetically.

    Set<Player> winners = Sets.newTreeSet();
    Set<Player> losers = Sets.newTreeSet();

    for (Player player : players) {
      Faction faction = player.getRole().getFaction();
      if (player.getRole().getType() == Role.MINION) {
        // Minion wins if the wolves win.
        faction = Faction.WOLVES;
      }
      if (winner == faction) {
        winners.add(player);
      } else {
        losers.add(player);
      }
    }

    bot.sendMessage("");
    bot.sendMessage("Winners");
    bot.sendMessage("");

    for (Player p : winners) {
      StringBuilder output = new StringBuilder();
      output.append(p.getName()).append(" (").append(p.getRole()).append(")");
      if (p.isAlive()) {
        output.append(" *ALIVE*");
      }
      bot.sendMessage(output.toString());
    }

    bot.sendMessage("");
    bot.sendMessage("Losers");
    bot.sendMessage("");

    for (Player p : losers) {
      StringBuilder output = new StringBuilder();
      output.append(p.getName()).append(" (").append(p.getRole()).append(")");
      if (p.isAlive()) {
        output.append(" *ALIVE*");
      }
      bot.sendMessage(output.toString());
    }
  }

}
