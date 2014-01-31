package wolf.role.classic;

import java.util.Collection;
import java.util.List;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.action.BotAction;
import wolf.arch.DisplayName;
import wolf.engine.Faction;
import wolf.engine.Player;
import wolf.engine.Time;
import wolf.engine.spell.KillSpell;
import wolf.role.GameRole;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

@DisplayName(value = "Wolf", plural = "Wolves")
public class Wolf extends GameRole {

  private Player currentKillTarget = null;

  @Override
  public Faction getFaction() {
    return Faction.WOLVES;
  }

  @Override
  protected void onNightBegins() {
    super.onNightBegins();

    getEngine().getBot().sendMessage(getPlayer(),
        "Tell me who you want to kill.  Message me '!kill [target]'");
  }

  @Override
  public String diedAtNightNotice() {
    return "died during the night and was a wolf.";
  }

  @Override
  public void end(Time time, Collection<GameRole> wolves) {
    if (time == Time.Night) {
      Player toKill = null;
      for (GameRole wolf : wolves) {
        toKill = ((Wolf) wolf).currentKillTarget;
        if (toKill != null) {
          break;
        }
      }
      KillSpell spell = new KillSpell(toKill);
      String message = toKill.getName() + " was a " + toKill.getRole().getNightKillNotice() + ".";
      for (GameRole wolf : wolves) {
        spell.getMessages().put(wolf.getPlayer(), message);
      }
      getEngine().cast(spell);
    }
    super.end(time, wolves);
  }

  @Override
  public void handlePrivateMessage(String message) {
    List<String> m = Lists.newArrayList(Splitter.on(' ').split(message));
    String command = m.get(0);

    if (isNight()) {
      if (!command.startsWith("!")) {
        getEngine().roleChat(this.getClass(), getPlayer(), "<WolfChat>", message);
        return;
      }
    }
    WolfBot.handleMessage(getEngine().getBot(), getCurrentActions(), null, getPlayer().getName(),
        message);
  }

  @Override
  protected void onNightEnds() {
    currentKillTarget = null;
  }

  @Override
  public boolean isFinished() {
    if (isNight()) {
      for (Player player : getEngine().getAlivePlayers(Wolf.class)) {
        if (((Wolf) player.getRole()).currentKillTarget != null) {
          return true;
        }
      }
      return false;
    }

    return super.isFinished();
  }

  @Override
  protected Collection<? extends BotAction> getCurrentActions() {
    if (getEngine().getTime() == Time.Night) {
      return ImmutableList.of(killAction);
    } else {
      return super.getCurrentActions();
    }
  }

  private final BotAction killAction = new BotAction(1) {
    @Override
    public String getCommandName() {
      return "kill";
    }

    @Override
    protected void execute(WolfBot bot, String sender, String command, List<String> args) {
      Player killTarget = getEngine().getPlayer(args.get(0));
      if (killTarget == null) {
        throw new WolfException("No such player: " + args.get(0));
      }

      if (!killTarget.isAlive()) {
        throw new WolfException("You can only kill players that are alive!");
      }

      currentKillTarget = killTarget;

      getEngine().getBot().sendMessage(sender,
          "Your wish to kill " + killTarget.getName() + " has been received.");
    }
  };

}
