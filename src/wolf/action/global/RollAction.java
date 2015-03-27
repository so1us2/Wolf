package wolf.action.global;

import java.util.List;
import java.util.Random;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.GameStage;
import wolf.model.stage.Stage;

public class RollAction extends Action {

  private static final Random rand = new Random();

  public RollAction(Stage stage) {
    super(stage, "roll", "min", "max");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    // Short circuit in the case of players attempting to roll at night
    Stage stage = getStage();
    if (stage instanceof GameStage && ((GameStage)stage).isNight()) {
      // probably send an admin message back to the individual player only saying that they can't roll at night
      // idk how to do that right now, so sending a public shaming message instead, seems appropriate
      getBot().sendMessage(invoker.getName() + " attempted to roll a dice at night. Shame on " + invoker.getName() + "!");
      return;
    }

    int min = 1;
    int max = 100;
    
    if(args.size() == 1){
      max = Integer.parseInt(args.get(0));
    } else if (args.size() == 2) {
      min = Integer.parseInt(args.get(0));
      max = Integer.parseInt(args.get(1));
    }

    if (min > max || min < 0) {
      throw new IllegalArgumentException();
    }

    int roll = min + rand.nextInt(max - min + 1);

    getBot().sendMessage(invoker.getName() + " rolled a " + roll + ". (" + min + "-" + max + ")");
  }

  @Override
  public String getDescription() {
    return "Rolls a die.";
  }

  @Override
  protected boolean argSizeMatters() {
    return false;
  }

}
