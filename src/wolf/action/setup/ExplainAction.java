package wolf.action.setup;

import java.util.List;
import java.util.Map;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.SetupStage;

import com.google.common.collect.Maps;

public class ExplainAction extends SetupAction {

  static final Map<String, String> topics = Maps.newLinkedHashMap();

  static {
    topics.put("Seer",
        "The Seer is a villager who can find out the affiliation of one player each night.");
    topics
        .put(
            "Medic",
            "The Medic is a villager who can protect one person from being killed each night. The medic may not protect the same person twice in a row.");

    topics.put("Villager",
        "The villagers are trying to find and kill the wolves before they get eaten.");
    topics
        .put("Wolf",
            "The wolves are trying to hide their identities by day and kill off the villagers at night.");
  }

  public ExplainAction(SetupStage stage) {
    super(stage, "explain", 1);
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    String topic = args.get(0);

    if(!topics.containsKey(topic)) {
      StringBuilder output = new StringBuilder();
      output.append("Unsupported Topic. Please try: ").append(topics.keySet());
      throw new WolfException(output.toString());
      // getBot().sendMessage(output.toString());
    } else {
      getBot().sendMessage(topic + ": " + topics.get(topic));
    }
  }

  @Override
  public String getDescription() {
    return "Get an explanation for different game parameters.";
  }

  @Override
  public String getUsage() {
    return "!" + getName() + " <topic>";
  }

}
