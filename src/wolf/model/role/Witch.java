package wolf.model.role;

import java.util.List;
import java.util.Map;

import org.testng.collections.Maps;

import wolf.WolfException;
import wolf.action.Action;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;

public class Witch extends AbstractRole {

  private Player readTarget;
  private final Map<Player, Role> readHistory = Maps.newHashMap();

  @Override
  public Player getSpecialTarget() {
    return readTarget;
  }

  @Override
  public void onNightBegins() {
    readTarget = null;
      getBot().sendMessage(getPlayer().getName(),
          "Who do you want to read?  Message /peek <target>");
  }

  private String getValueFor(Player p) {
    return readHistory.get(p).name();
  }

  @Override
  public void onNightEnds() {
    if (readTarget == null) {
      return;
    }
    Player player = getPlayer();
    Role role = player.getRole().getType();
    if (getStage().isCorrupterTarget(getPlayer())) {
      if (role.getFaction().equals(Faction.WOLVES)) {
        role = Role.VILLAGER;
      } else {
        role = Role.WOLF;
      }
    }
    readHistory.put(readTarget, role);

    getStage().getBot().sendMessage(
        player.getName(),
        readTarget.getName() + " is a " + getValueFor(readTarget)
                + ".");
  }

  @Override
  public void onPlayerSwitch() {
    // send new player a list of all previous reads.
    super.onPlayerSwitch();
    int i = 0;
    for (Player p : readHistory.keySet()) {
      getStage().getBot().sendMessage(getPlayer().getName(),
          "Night " + i++ + ": " + p.getName() + " - " + getValueFor(p));
    }
  }

  @Override
  public boolean isFinishedWithNightAction() {
      return readTarget != null;
  }

  @Override
  public List<Action> getNightActions() {
    return ImmutableList.<Action>of(readAction);
  }

  @Override
  public String getDescription() {
    return "The Witch may find out the roles of the dead.";
  }

  private final Action readAction = new Action("read", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Witch.this.getStage();
      Player read = stage.getPlayer(args.get(0));
      if (read == getPlayer()) {
        throw new WolfException("You cannot read yourself.");
      } else if (stage.getPlayers().contains(read)) {
        throw new WolfException("You cannot read living players.");
      } else {
        readTarget = read;
        stage.getBot().sendMessage(invoker.getName(),
            "Your wish to read " + readTarget + " has been received.");
      }
    }

    @Override
    public String getDescription() {
      return "Discover the role of a dead person.";
    }
  };

}
