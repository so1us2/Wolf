package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;

public class Wolf extends AbstractRole {

  private Player killTarget;

  @Override
  public void onGameStart() {
    super.onGameStart();
    getBot().sendMessage(getPlayer().getName(),
        "The wolves are: " + getStage().getPlayers(Role.WOLF));
  }

  @Override
  public void onNightBegins() {
    killTarget = null;
    getBot().sendMessage(getPlayer().getName(),
        "Who do you want to kill?  Message me /kill <target>");
  }

  @Override
  public String getSettingsExplanation() {
    StringBuilder output = new StringBuilder();
    String mode = getStage().getSetting("TELL_WOLVES_ON_KILL");
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
    return killTarget != null;
  }

  @Override
  public List<Action> getNightActions() {
    return ImmutableList.<Action>of(killAction);
  }
  
  @Override
  public Player getTarget() {
    return killTarget;
  }

  @Override
  public String getKillMessage() {
    return "has been ripped apart";
  }

  public void handleChat(Player sender, String message) {
    if (getStage().isNight()) {
      // wolf-chat
      wolfChat(sender, message);
    } else {
      super.handleChat(sender, message);
    }
  }

  public void wolfChat(Player sender, String message) {
    for (Player wolf : getStage().getPlayers(Role.WOLF)) {
      if (wolf != sender) {
        getBot().sendMessage(wolf.getName(), "<WolfChat> " + sender + ": " + message);
      }
    }
  }

  @Override
  public String getDescription() {
    return "The Wolves kill a villager every night. They win when their numbers equal those of the villagers. They can communicate openly at night using wolf chat.";
  }

  private Action killAction = new Action("kill", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Wolf.this.getStage();

      killTarget = stage.getPlayer(args.get(0));

      wolfChat(invoker, invoker + " votes to kill " + killTarget);

      stage.getBot().sendMessage(invoker.getName(),
          "Your wish to kill " + killTarget + " has been received.");
    }

    @Override
    public String getDescription() {
      return "Feast on their flesh! The target will not awaken in the morning...";
    }
  };

}
