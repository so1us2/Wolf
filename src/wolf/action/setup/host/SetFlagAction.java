package wolf.action.setup.host;

import java.util.List;

import wolf.WolfException;
import wolf.action.setup.SetupAction;
import wolf.model.Player;
import wolf.model.Settings;
import wolf.model.stage.SetupStage;

public class SetFlagAction extends SetupAction {

  public SetFlagAction(SetupStage stage) {
    super(stage, "setflag", "flag", "option");
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    String settingName = args.get(0).toUpperCase();
    String option = args.get(1).toUpperCase();
    if (!getStage().getConfig().getSettings().containsKey(settingName)) {
      throw new WolfException(settingName + " is not a valid setting.");
    }
    if (settingName.equals("TIME_LIMIT")) {
      try {
        Integer.parseInt(option);
      } catch (Exception e) {
        throw new WolfException(option + " is not a valid option for " + settingName);
      }
    } else if (!Settings.get(settingName).getOptions().contains(option)) {
      throw new WolfException(option + " is not a valid option for " + settingName);
    }
    getStage().getConfig().getSettings().put(settingName, option);
    getStage().getBot().sendMessage(settingName + " set to " + option);
  }

  @Override
  public String getDescription() {
    return "Specify an option for a setting.";
  }

}
