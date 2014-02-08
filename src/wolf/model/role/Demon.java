package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;

public class Demon extends AbstractRole {

  public static String NO_KILL_MESSAGE = "You resist the urge to rip someone apart.";

  private boolean hasActed = false;
  private Player killTarget;

  @Override
  public void onNightBegins() {
    hasActed = false;
    killTarget = null;
    getBot().sendMessage(getPlayer().getName(),
        "Who do you want to kill?  Message me /kill <target> or /pass to kill no one.");
  }

  @Override
  public String getSettingsExplanation() {
    StringBuilder output = new StringBuilder();
    String mode = getStage().getSetting("TELL_DEMON_ON_KILL");
    if (mode.equals("NONE")) {
      output.append("You will find out nothing about the people you kill.");
    } else if (mode.equals("FACTION")) {
      output.append("You will find out the faction of people you kill.");
    } else if (mode.equals("ROLE")) {
      output.append("You will find out the role of people you kill.");
    }
    return output.toString();
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return hasActed;
  }

  @Override
  public List<Action> getNightActions() {
    return ImmutableList.<Action>of(killAction, passAction);
  }

  @Override
  public Player getKillTarget() {
    return killTarget;
  }

  @Override
  public String getKillMessage() {
    return "is a soulless husk";
  }

  @Override
  public String getDescription() {
    return "The Demon is a solo role that wins by killing everyone else. Any other character that targets the demon at night will die.";
  }

  private Action killAction = new Action("kill", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Demon.this.getStage();

      hasActed = true;
      killTarget = stage.getPlayer(args.get(0));
      stage.getBot().sendMessage(invoker.getName(), "You plan to kill " + killTarget + ".");
    }

    @Override
    public String getDescription() {
      return "You may kill someone every night if you so choose.";
    }
  };

  private Action passAction = new Action("pass") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Demon.this.getStage();

      hasActed = true;
      killTarget = null;
      stage.getBot().sendMessage(invoker.getName(), NO_KILL_MESSAGE);
    }

    @Override
    public String getDescription() {
      return "You refrain from slaughtering anyone for the night.";
    }
  };


}
