package wolf.model.role;

import java.util.List;

import wolf.WolfException;
import wolf.action.Action;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Priest extends AbstractRole {


  private Player protectTarget;
  private final List<Player> protectHistory = Lists.newArrayList();

  @Override
  public void onNightBegins() {
    protectTarget = null;
    if (!canProtectSomeone()) {
      getBot().sendMessage(getPlayer().getName(), "There are no legal targets to protect tonight.");
    } else {
      getBot().sendMessage(getPlayer().getName(),
          "Who do you want to protect?  Message me !protect <target>");
    }
  }

  @Override
  public void onNightEnds() {
    protectHistory.add(protectTarget);
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return protectTarget != null;
  }

  @Override
  public List<Action> getNightActions() {
    if (!canProtectSomeone()) {
      return ImmutableList.of();
    }
    return ImmutableList.<Action>of(protectAction);
  }

  @Override
  public Player getTarget() {
    return protectTarget;
  }

  @Override
  public String getDescription() {
    return "The Priest protects a player each night, preventing that player from being killed.";
  }

  private boolean canProtectSomeone() {
    if (getStage().getSetting("PROTECTION_MODE").equals("ONCE_PER_GAME")) {
      for (Player p : getStage().getPlayers()) {
        if (!protectHistory.contains(p)) {
          return true;
        }
      }
    } else {
      return true;
    }
    return false;
  }

  private Action protectAction = new Action("protect", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Priest.this.getStage();
      protectTarget = stage.getPlayer(args.get(0));

      if (Objects.equal(protectTarget, invoker)) {
        // make sure we are allowed to protect ourselves
        if (stage.getSetting("SELF_PROTECT").equals("NO")) {
          throw new WolfException("You are not allowed to protect yourself.");
        }
      }

      String mode = stage.getSetting("PROTECTION_MODE");
      if (mode.equals("EVERY_OTHER_NIGHT")) {
        if (protectTarget == getLastProtect()) {
          Player fail = protectTarget;
          protectTarget = null;
          throw new WolfException("You cannot protect " + fail.getName() + " twice in a row.");
        }
      } else if (mode.equals("ONCE_PER_GAME")) {
        if (protectHistory.contains(protectTarget)) {
          Player fail = protectTarget;
          protectTarget = null;
          throw new WolfException("You have already protected " + fail.getName() + " this game.");
        }
      } else if (mode.equals("NO_RULES")) {}

      stage.getBot().sendMessage(invoker.getName(),
          "Your wish to protect " + protectTarget + " has been received.");
    }

    private Player getLastProtect() {
      if (protectHistory.size() > 0) {
        return Iterables.getLast(protectHistory);
      }
      return null;
    }

    @Override
    public String getDescription() {
      return "Protects the target from being killed by wolves tonight.";
    }
  };

}
