package wolf.model.role;

import java.util.List;

import wolf.WolfException;
import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class Witch extends AbstractRole {

  private Player readTarget;
  private final List<Player> readHistory = Lists.newArrayList();

  @Override
  public Player getTarget() {
    return readTarget;
  }

  @Override
  public void onNightBegins() {
    readTarget = null;
    if (!hasReadEveryone()) {
      getBot().sendMessage(getPlayer().getName(),
          "Who do you want to read?  Message /peek <target>");
    } else {
      getBot().sendMessage(getPlayer().getName(), "You have read everyone.");
      int i = 1;
      for (Player p : readHistory) {
        getStage().getBot().sendMessage(getPlayer().getName(),
            "Night " + i++ + ": " + p.getName() + " - "
                + p.getRole().getType().name().toLowerCase());
      }
    }
  }

  private boolean hasReadEveryone() {
    for (Player p : getStage().getDeadPlayers()) {
      if (!readHistory.contains(p) && p != getPlayer()) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void onNightEnds() {
    if (readTarget == null) {
      return;
    }
    Player player = getPlayer();
    readHistory.add(readTarget);

    getStage().getBot().sendMessage(
        player.getName(),
            readTarget.getName() + " is a " + readTarget.getRole().getType().name().toLowerCase()
                + ".");
  }

  @Override
  public void onPlayerSwitch() {
    // send new player a list of all previous reads.
    super.onPlayerSwitch();
    int i = 0;
    for (Player p : readHistory) {
      getStage().getBot().sendMessage(getPlayer().getName(),
          "Night " + i++ + ": " + p.getName() + " - " + p.getRole().getFaction());
    }
  }

  @Override
  public boolean isFinishedWithNightAction() {
    if (!hasReadEveryone()) {
      return readTarget != null;
    }
    return true;
  }

  @Override
  public List<Action> getNightActions() {
    if (hasReadEveryone()) {
      return ImmutableList.of();
    }
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
      }
      if (readHistory.contains(read)) {
        throw new WolfException("You have already read " + read + ". They are a "
            + read.getRole().getType().name().toLowerCase() + ".");
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
