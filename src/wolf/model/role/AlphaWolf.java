package wolf.model.role;

import java.util.List;
import java.util.Map;
import wolf.WolfException;
import wolf.action.Action;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.GameStage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class AlphaWolf extends AbstractWolfRole {
  private Player sniffTarget;
  private final Map<Player, Role> sniffHistory = Maps.newHashMap();

  @Override
  public Player getSpecialTarget() {
    return sniffTarget;
  }

  @Override
  public void onNightBegins() {
    super.onNightBegins();
    sniffTarget = null;
      getBot().sendMessage(getPlayer().getName(),
          "Who do you want to sniff?  Message /sniff <target>");
  }

  private String getValueFor(Player p) {
    return sniffHistory.get(p).name().toLowerCase();
  }

  @Override
  public void onNightEnds() {
    if (sniffTarget == null) {
      return;
    }
    Player player = getPlayer();
    sniffHistory.put(sniffTarget, sniffTarget.getRole().getType());
    getStage().getBot().sendMessage(player.getName(),
        sniffTarget.getName() + " smells like a " + getValueFor(sniffTarget)
            + ".");
  }

  @Override
  public void onPlayerSwitch() {
    // send new player a list of all previous peeks.
    super.onPlayerSwitch();
    int i = 0;
    for (Player p : sniffHistory.keySet()) {
      getStage().getBot().sendMessage(getPlayer().getName(),
          "Night " + i++ + ": " + p.getName() + " - " + getValueFor(p));
    }
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return super.isFinishedWithNightAction() && sniffTarget != null;
  }

  @Override
  public List<Action> getNightActions() {
    List<Action> ret = Lists.newArrayList();
    ret.addAll(super.getNightActions());
    ret.add(sniffAction);
    return ret;
  }

  @Override
  public String getDescription() {
    return "The Alpha Wolf uses its sense of smell to determine the role of a player each night.";
  }

  private final Action sniffAction = new Action("sniff", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = AlphaWolf.this.getStage();
      Player sniff = stage.getPlayer(args.get(0));
      if (sniff == getPlayer()) {
        throw new WolfException("You cannot sniff yourself.");
      } else if (sniff.getRole().getFaction() == Faction.WOLVES) {
        throw new WolfException("You cannot sniff a wolf.");
      } else {
        sniffTarget = sniff;
        stage.getBot().sendMessage(invoker.getName(),
            "Your wish to sniff " + sniffTarget + " has been received.");
      }
    }

    @Override
    public String getDescription() {
      return "Use a player's scent to determine his or her role.";
    }
  };

}
