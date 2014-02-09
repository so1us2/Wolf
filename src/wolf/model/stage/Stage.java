package wolf.model.stage;

import java.util.List;
import java.util.Set;

import wolf.action.Action;
import wolf.bot.IBot;
import wolf.model.Player;

import com.google.common.collect.ImmutableSet;

public abstract class Stage {

  private static final Set<String> admins = ImmutableSet.of("satnam", "TomM");

  private final IBot bot;

  public Stage(IBot bot) {
    this.bot = bot;
  }

  public void handleChat(IBot bot, String sender, String message) {
    bot.sendToAll(sender, message);
  }

  public void handle(IBot bot, String sender, String command, List<String> args) {
    Player player = getPlayer(sender);

    Action action = getActionForCommand(command, player);

    if (action == null) {
      String message = "Invalid command: " + command;
      bot.sendMessage(sender, message);
      action = getActionForCommand("commands", player);
    }

    action.apply(player, args);
  }

  public abstract Iterable<Player> getAllPlayers();

  public Player getPlayer(String name) {
    return new Player(name, admins.contains(name));
  }

  private Action getActionForCommand(String command, Player player) {
    for (Action a : getAvailableActions(player)) {
      if (a.getName().equalsIgnoreCase(command)) {
        return a;
      }
    }
    return null;
  }

  public abstract List<Action> getAvailableActions(Player player);

  public IBot getBot() {
    return bot;
  }

}
