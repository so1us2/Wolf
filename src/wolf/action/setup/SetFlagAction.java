package wolf.action.setup;

import java.util.List;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.Settings;
import wolf.model.stage.SetupStage;

public class SetFlagAction extends SetupAction {

  public SetFlagAction(SetupStage stage) {
    super(stage, "setflag", "flag", "option");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    String settingName = args.get(0).toUpperCase();
    String option = args.get(1).toUpperCase();
    // is this better than using the static method to see if the setting exists?
    if (!getStage().getConfig().getSettings().containsKey(settingName)) {
      throw new WolfException(settingName + " is not a valid setting.");
    }
    if (!Settings.get(settingName).getOptions().contains(option)) {
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
