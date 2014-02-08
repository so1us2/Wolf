package wolf.action.setup;

import java.util.Collection;
import java.util.List;

import org.testng.collections.Lists;

import wolf.model.Player;
import wolf.model.Setting;
import wolf.model.Settings;
import wolf.model.stage.SetupStage;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;

// Later we will add this into menus but for now just list them all.
public class ListSettingsAction extends SetupAction {

  public ListSettingsAction(SetupStage stage) {
    super(stage, "settings");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Multimap<String, Setting> settings = Settings.getSettingsByCategory();

    for (String c : settings.keySet()) {
      getStage().getBot().sendMessage(invoker.getName(), "CATEGORY " + c.toUpperCase());
      Collection<Setting> matches = settings.get(c);
      for (Setting s : matches) {
        List<String> options = Lists.newArrayList(s.getOptions());
        String current = getStage().getConfig().getSettings().get(s.getName());
        options.remove(current);

        StringBuilder output = new StringBuilder();
        output.append(s.getName()).append(". Current: ").append(current)
            .append(". Other options: ").append("[")
            .append(Joiner.on(", ").join(options)).append("]");
        getStage().getBot().sendMessage(invoker.getName(), output.toString());
      }
    }

  }

  @Override
  public String getDescription() {
    return "List all game settings that can be configured.";
  }

}
