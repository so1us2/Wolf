package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;

public class Vigilante extends AbstractRole {

  public static String HOLD_FIRE_MESSAGE = "You holster your pistol.";
  public static String CORRUPTED_MESSAGE = "Your gun jams and you fail to fire.";
  // public static String KILL_CONFIRMED_MESSAGE = "You strike %s square between the eyes.";

  private boolean hasFired = false;
  private boolean hasActed = false;
  private Player killTarget;

  @Override
  public void onNightBegins() {
    if (!hasFired) {
    killTarget = null;
    getBot().sendMessage(getPlayer().getName(),
              "Do you want to use your shot?  Message me /shoot <target> to shoot or /pass to hold fire.");
    }
  }

  @Override
  public void onNightEnds() {
    if (killTarget != null) {
      hasFired = true;
      killTarget = null;
    }
    hasActed = false;
  }

  @Override
  public String getSettingsExplanation() {
    StringBuilder output = new StringBuilder();
    String mode = getStage().getSetting("TELL_VIG_ON_KILL");
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
    return hasActed || hasFired;
  }

  @Override
  public List<Action> getNightActions() {
    if (hasFired) {
      return ImmutableList.of();
    } else {
      return ImmutableList.<Action>of(shootAction, passAction);
    }
  }

  @Override
  public Player getKillTarget() {
    return killTarget;
  }

  public void corrupt() {
    if (killTarget != null) {
      getStage().getBot().sendMessage(getPlayer().getName(), CORRUPTED_MESSAGE);
    }
    killTarget = null;
  }

  @Override
  public String getKillMessage() {
    return "has a single bullet wound in the forehead";
  }

  @Override
  public String getDescription() {
    return "Once per game, the Vigilante can kill a player at night.";
  }

  private Action shootAction = new Action("shoot", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Vigilante.this.getStage();

      hasActed = true;
      killTarget = stage.getPlayer(args.get(0));
      stage.getBot().sendMessage(invoker.getName(), "You aim at " + killTarget + ".");
    }

    @Override
    public String getDescription() {
      return "You have a single bullet. Use it wisely.";
    }
  };

  private Action passAction = new Action("pass") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Vigilante.this.getStage();

      hasActed = true;
      killTarget = null;
      stage.getBot().sendMessage(invoker.getName(), HOLD_FIRE_MESSAGE);
    }

    @Override
    public String getDescription() {
      return "You hold your fire and do not kill anyone.";
    }
  };


}
