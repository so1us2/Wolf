package wolf.model.role;

import java.util.List;
import java.util.Map;
import wolf.WolfException;
import wolf.action.Action;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.GameStage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

public class Seer extends AbstractRole {

  private Player peekTarget;
  private final Map<Player, Faction> peekHistory = Maps.newHashMap();

  @Override
  public void onGameStart() {
    super.onGameStart();

    if (getStage().getSetting("PRE_GAME_PEEK").equals("NO")) {
      return;
    }
    String mode = getStage().getSetting("PRE_GAME_PEEK_MODE");
    List<Player> villagers;
    if (mode.equals("REGULAR_VILLAGERS")) {
      villagers = getStage().getPlayers(Role.VILLAGER);
    } else if (mode.equals("ALL_VILLAGERS")) {
      villagers = getStage().getPlayers(Faction.VILLAGERS);
      if (getStage().getSetting("FIRST_PEEK_MINION").equals("NO")) {
        villagers.removeAll(getStage().getPlayers(Role.MINION));
      }
      villagers.remove(getPlayer());
    } else {
      return;
    }

    if (villagers.isEmpty()) {
      getBot().sendMessage(getPlayer().getName(), "There were no villagers to peek.");
    } else {
      Player peek = villagers.get((int) (Math.random() * villagers.size()));
      peekHistory.put(peek, peek.getRole().getFaction());
      getBot().sendMessage(getPlayer().getName(), peek.getName() + " is a villager.");
    }
  }

  private String getValueFor(Player p) {
    return peekHistory.get(p).getSingularForm().toLowerCase();
  }

  @Override
  public String getSettingsExplanation() {
    StringBuilder output = new StringBuilder();
    if (getStage().getSetting("PRE_GAME_PEEK").equals("NO")) {
      output.append("You will not get a random peek before the game starts.");
    } else {
      String mode = getStage().getSetting("PRE_GAME_PEEK_MODE");
      output.append("Before the game, ");
      if (mode.equals("REGULAR_VILLAGERS")) {
        output.append("you will get the name of a random VILLAGER (no special roles).");
      } else if (mode.equals("ALL_VILLAGERS")) {
        output.append("you will get the name of a random human (could have special role).");
        if (getStage().getSetting("FIRST_PEEK_MINION").equals("NO")) {
          output.append(" It could be a Minion.");
        }
      }
    }
    return output.toString();
  }

  @Override
  public Player getSpecialTarget() {
    return peekTarget;
  }

  @Override
  public void onNightBegins() {
    peekTarget = null;
    getBot().sendMessage(getPlayer().getName(), "Who do you want to peek?  Message /peek <target>");
  }

  @Override
  public void onNightEnds() {
    if (peekTarget == null) {
      return;
    }
    Player player = getPlayer();
    Faction view = peekTarget.getRole().getFaction();
    if (getStage().isCorrupterTarget(player)) {
      if (peekTarget.getRole().getFaction().equals(Faction.WOLVES)) {
        view = Faction.VILLAGERS;
      } else {
        view = Faction.WOLVES;
      }
    }

    peekHistory.put(peekTarget, view);
    getStage().getBot().sendMessage(player.getName(),
        peekTarget.getName() + " is a " + getValueFor(peekTarget) + ".");
  }

  @Override
  public void onPlayerSwitch() {
    // send new player a list of all previous peeks.
    super.onPlayerSwitch();
    int i = 0;
    for (Player p : peekHistory.keySet()) {
      getStage().getBot().sendMessage(getPlayer().getName(),
          "Night " + i++ + ": " + p.getName() + " - " + getValueFor(p));
    }
  }

  @Override
  public boolean isFinishedWithNightAction() {
    return peekTarget != null;
  }

  @Override
  public List<Action> getNightActions() {
    return ImmutableList.<Action>of(peekAction);
  }

  @Override
  public String getDescription() {
    return "The Seer peeks a player each night and finds out if the player is a villager or a wolf.";
  }

  private final Action peekAction = new Action("peek", "target") {
    @Override
    protected void execute(Player invoker, List<String> args) {
      GameStage stage = Seer.this.getStage();
      Player peek = stage.getPlayer(args.get(0));
      if (peek == getPlayer()) {
        throw new WolfException("You cannot peek yourself.");
      } else {
        peekTarget = peek;
        stage.getBot().sendMessage(invoker.getName(),
            "Your wish to peek " + peekTarget + " has been received.");
      }
    }

    @Override
    public String getDescription() {
      return "Gain information on whether that person is one of the villagers or wolves.";
    }
  };

}
