package wolf.action.setup.host;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import wolf.WolfException;
import wolf.action.setup.CurrentSetupAction;
import wolf.action.setup.SetupAction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.SetupStage;

import static wolf.model.Role.PRIEST;
import static wolf.model.Role.SEER;
import static wolf.model.Role.VILLAGER;
import static wolf.model.Role.WOLF;

public class SetPlayersAction extends SetupAction {

  public SetPlayersAction(SetupStage stage) {
    super(stage, "setplayers", "num_players");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    int numPlayers = Integer.parseInt(args.get(0));

    if (numPlayers < 3) {
      throw new WolfException("Cannot create a game with less than 3 players!");
    }

    Map<Role, Integer> roles = Maps.newHashMap();

    if (numPlayers == 3) {
      roles.put(VILLAGER, 2);
      roles.put(WOLF, 1);
    } else if (numPlayers == 4) {
      roles.put(VILLAGER, 3);
      roles.put(WOLF, 1);
    } else if (numPlayers == 5) {
      LoadConfigAction.loadConfig(getStage().getConfig(), "fives");
      return;
    } else if (numPlayers == 6) {
      roles.put(VILLAGER, 2);
      roles.put(SEER, 1);
      roles.put(PRIEST, 1);
      roles.put(WOLF, 2);
    } else if (numPlayers == 7) {
      roles.put(VILLAGER, 4);
      roles.put(SEER, 1);
      roles.put(WOLF, 2);
    } else if (numPlayers == 8) {
      roles.put(VILLAGER, 5);
      roles.put(SEER, 1);
      roles.put(WOLF, 2);
    } else if (numPlayers == 9) {
      roles.put(VILLAGER, 6);
      roles.put(SEER, 1);
      roles.put(WOLF, 2);
    } else if (numPlayers == 10) {
      roles.put(VILLAGER, 5);
      roles.put(SEER, 1);
      roles.put(PRIEST, 1);
      roles.put(WOLF, 3);
    } else if (numPlayers == 11) {
      roles.put(VILLAGER, 6);
      roles.put(SEER, 1);
      roles.put(PRIEST, 1);
      roles.put(WOLF, 3);
    } else if (numPlayers == 12) {
      roles.put(VILLAGER, 6);
      roles.put(SEER, 1);
      roles.put(PRIEST, 1);
      roles.put(WOLF, 4);
    } else if (numPlayers == 13) {
      roles.put(VILLAGER, 7);
      roles.put(SEER, 1);
      roles.put(PRIEST, 1);
      roles.put(WOLF, 4);
    } else {
      int numWolves = 1 + numPlayers / 4;
      roles.put(VILLAGER, numPlayers - 2 - numWolves);
      roles.put(WOLF, numWolves);
      roles.put(SEER, 1);
      roles.put(PRIEST, 1);
    }

    getStage().getConfig().setRoles(roles);

    getBot().sendMessage("Number of players set to " + numPlayers);
    getBot().sendMessage(CurrentSetupAction.printRoles(getStage(), getBot()));
  }

  @Override
  protected boolean requiresHost() {
    return true;
  }

  @Override
  public String getDescription() {
    return "Chooses a fair setup for the given number of players.";
  }

}
