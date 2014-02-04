package wolf.action.moderator;

import java.util.List;

import wolf.model.Player;


public class ReminderAction extends wolf.action.Action {

  public ReminderAction(String name, String[] argsNames) {
    super("remind");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {



  }

  @Override
  public String getDescription() {
    return "Remind players who need to act to do so.";
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

}
