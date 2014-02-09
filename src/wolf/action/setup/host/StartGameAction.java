package wolf.action.setup.host;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import wolf.WolfException;
import wolf.action.setup.SetupAction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.role.AbstractRole;
import wolf.model.stage.GameStage;
import wolf.model.stage.SetupStage;

import com.google.common.collect.Lists;

public class StartGameAction extends SetupAction {

  public StartGameAction(SetupStage stage) {
    super(stage, "start");
  }

  @Override
  public boolean requiresHost() {
    return true;
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    int numPlayers = getStage().getPlayers().size();
    int neededPlayers = getStage().getConfig().getPlayersNeeded();

    if (neededPlayers == 0) {
      throw new WolfException("No game configuration loaded.");
    }
    if (numPlayers < neededPlayers) {
      throw new WolfException("You only have " + numPlayers + ", but you need " + neededPlayers
          + " to start the game.");
    }
    if (numPlayers > neededPlayers) {
      throw new WolfException("You currently have " + numPlayers
          + ", which is too many. Once there are " + neededPlayers + " you may start the game.");
    }

    startGame();
  }

  private void startGame() {
    getBot().muteAll();

    assignRoles();

    getBot().setStage(new GameStage(getBot(), getStage().getConfig(), getStage().getPlayers()));
  }

  private void assignRoles() {
    getBot().sendMessage("Assigning roles...");

    List<Role> roles = Lists.newArrayList();
    for (Entry<Role, Integer> e : getStage().getConfig().getRoles().entrySet()) {
      roles.addAll(Collections.nCopies(e.getValue(), e.getKey()));
    }
    Collections.shuffle(roles);

    int c = 0;
    for (Player player : getStage().getPlayers()) {
      player.setRole(AbstractRole.create(roles.get(c++), player));
      getBot().sendMessage(player.getName(), "You are a " + player.getRole() + ".");
    }
  }

  @Override
  public String getDescription() {
    return "Starts the game.";
  }



}
