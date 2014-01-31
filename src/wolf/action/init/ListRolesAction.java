package wolf.action.init;

import java.util.List;
import java.util.Map.Entry;

import wolf.WolfBot;
import wolf.arch.Utils;
import wolf.role.GameRole;

public class ListRolesAction extends AbstractInitAction {

  @Override
  public String getCommandName() {
    return "roles";
  }

  @Override
  protected void execute(WolfBot bot, String sender, String command, List<String> args) {

    if (initializer.getRoleCountMap().isEmpty()) {
      bot.sendMessage("No roles have been added.");
      return;
    }

    StringBuilder sb = new StringBuilder();

    sb.append("Roles: ");

    for (Entry<Class<? extends GameRole>, Integer> entry : initializer.getRoleCountMap().entrySet()) {
      sb.append(Utils.getDisplayName(entry.getKey(), false)).append("(").append(entry.getValue())
          .append(")").append(", ");
    }

    sb.delete(sb.length() - 2, sb.length());

    bot.sendMessage(sb.toString());
  }
}
