package wolf.model.role;

import java.util.List;

import wolf.action.Action;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;

public abstract class AbstractWolfRole extends AbstractRole {

  protected Player killTarget;

  @Override
  public void onGameStart() {
    getBot().sendMessage(getPlayer().getName(),
        "The wolves are: " + getStage().getPlayers(Faction.WOLVES));
  }

  public void wolfChat(Player sender, String message) {
    if (getStage().getSetting("SILENT_GAME").equals("YES")) {
      getBot().sendMessage(getPlayer().getName(), "Wolf chat is disabled for this silent game.");
      return;
    }
    for (Player wolf : getStage().getPlayers(Faction.WOLVES)) {
      // if (wolf != sender) {
        getBot().sendMessage(wolf.getName(), "<WolfChat> " + sender + ": " + message);
      // }
    }
  }

  @Override
  public void onNightBegins() {
    killTarget = null;
    getBot().sendMessage(getPlayer().getName(),
        "Who do you want to kill?  Message me /kill <target>");
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
  public Player getKillTarget() {
    return killTarget;
  }

  @Override
  public String getKillMessage() {
    return "has been ripped apart";
  }

  @Override
  public void handleChat(Player sender, String message) {
    if (getStage().isNight()) {
      // wolf-chat
      wolfChat(sender, message);
    } else {
      super.handleChat(sender, message);
    }
  }


  protected Action killAction = new Action("kill", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = AbstractWolfRole.this.getStage();

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
