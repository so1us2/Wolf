package wolf.model.role;

import java.util.List;

import wolf.WolfException;
import wolf.action.Action;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.stage.GameStage;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Corrupter extends AbstractWolfRole {

  private Player corruptTarget;
  private final List<Player> corruptHistory = Lists.newArrayList();

  @Override
  public void onNightBegins() {
    super.onNightBegins();
    corruptTarget = null;
    if (!canCorruptSomeone()) {
      getBot().sendMessage(getPlayer().getName(), "There are no legal targets to corrupt tonight.");
    } else {
      getBot().sendMessage(getPlayer().getName(),
          "Who do you want to corrupt?  Message me /corrupt <target>");
    }
  }

  @Override
  public void onNightEnds() {
    corruptHistory.add(corruptTarget);
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return (super.isFinishedWithNightAction() && (corruptTarget != null || !canCorruptSomeone()));
  }

  @Override
  public List<Action> getNightActions() {
    if (!canCorruptSomeone()) {
      return ImmutableList.copyOf(super.getNightActions());
    }
    List<Action> ret = Lists.newArrayList();
    ret.addAll(super.getNightActions());
    ret.add(corruptAction);
    return ret;
  }

  @Override
  public Player getSpecialTarget() {
    return corruptTarget;
  }

  private boolean canCorruptSomeone() {
    if (getStage().getSetting("CORRUPTION_MODE").equals("ONCE_PER_GAME")) {
      for (Player p : getStage().getPlayers()) {
        if (!corruptHistory.contains(p)) {
          return true;
        }
      }
    } else {
      return true;
    }
    return false;
  }

  @Override
  public String getDescription() {
    return "The Corrupter negates or inverts the powers of the target each night.";
  }

  private Action corruptAction = new Action("corrupt", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Corrupter.this.getStage();
      corruptTarget = stage.getPlayer(args.get(0));

      if (corruptTarget.getRole().getFaction() == Faction.WOLVES) {
        throw new WolfException("You cannot corrupt a wolf.");
      }

      String mode = stage.getSetting("CORRUPTION_MODE");
      if (mode.equals("EVERY_OTHER_NIGHT")) {
        if (corruptTarget == getLastCorrupt()) {
          Player fail = corruptTarget;
          corruptTarget = null;
          throw new WolfException("You cannot corrupt " + fail.getName() + " twice in a row.");
        }
      } else if (mode.equals("ONCE_PER_GAME")) {
        if (corruptHistory.contains(corruptTarget)) {
          Player fail = corruptTarget;
          corruptTarget = null;
          throw new WolfException("You have already corrupted " + fail.getName() + " this game.");
        }
      } else if (mode.equals("NO_RULES")) {}

      stage.getBot().sendMessage(invoker.getName(),
          "Your wish to corrupt " + corruptTarget + " has been received.");
    }

    private Player getLastCorrupt() {
      if (corruptHistory.size() > 0) {
        return Iterables.getLast(corruptHistory);
      }
      return null;
    }

    @Override
    public String getDescription() {
      return "Corrupts the target, warping their special powers.";
    }
  };

}
