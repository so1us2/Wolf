package wolf.action.game.admin;

import java.util.List;

import wolf.action.game.GameAction;
import wolf.model.Player;
import wolf.model.stage.GameStage;

public class ModkillPlayerAction extends GameAction {

  public ModkillPlayerAction(GameStage stage) {
    super(stage, "modkill", "target");
  }

  @Override
  public boolean requiresAdmin() {
    return true;
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    if (getStage().isNight()) {
      getStage().getBot().sendMessage(invoker.getName(),
          "This command may only be used by day (for now)");
    }
    Player target = getStage().getPlayer(args.get(0));
    System.out
        .println("MODKILLING " + target.getName() + " who is a " + target.getRole().getType());
    target.setAlive(false);
    getStage().getVotesToDayKill().remove(target);

    getStage().getBot().sendMessage(
        invoker.getName().toUpperCase() + " OBLITERATES " + target.getName().toUpperCase()
            + " IN A PILLAR OF BANEFIRE!");
    getStage().checkForWinner();

    getBot().onPlayersChanged();

    getBot().sendMessage(invoker.getName(),
        target.getName() + " was a " + target.getRole().getType());
  }

  @Override
  public String getDescription() {
    return "Eliminate a player from the current game.";
  }



}
