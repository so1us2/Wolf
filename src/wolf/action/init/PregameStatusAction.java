package wolf.action.init;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.WolfBot;
import wolf.role.GameRole;

public class PregameStatusAction extends AbstractInitAction {

  @Override
  public String getCommandName() {
    return "status";
  }

  @Override
  protected void execute(WolfBot bot, String sender, String command, List<String> args) {

    Map<Class<? extends GameRole>, Integer> roleCounts = initializer.getRoleCountMap();
    int numPlayers = initializer.getNamePlayerMap().size();

    Collection<Integer> counts = roleCounts.values();

    int i = 0;

    for (Integer c : counts) {
      i += c;
    }

    bot.sendMessage("You have " + numPlayers + " players. You need " + i + " total players.");
  }

  protected void printRoles(WolfBot bot) {

    for (Entry<Class<? extends GameRole>, Integer> entry : initializer.getRoleCountMap().entrySet()) {
      bot.sendMessage(entry.getKey() + ": " + entry.getValue());
    }

  }
}
