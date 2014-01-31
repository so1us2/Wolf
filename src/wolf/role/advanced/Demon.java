package wolf.role.advanced;

import java.util.Collection;
import java.util.List;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.action.BotAction;
import wolf.engine.Faction;
import wolf.engine.Player;
import wolf.engine.Time;
import wolf.engine.spell.KillSpell;
import wolf.role.GameRole;

import com.google.common.collect.ImmutableList;

public class Demon extends wolf.role.GameRole {

  protected Player phylactery;
  protected Player currentKillTarget;

  public Player getPhylactery() {
    return phylactery;
  }

  public void setPhylactery(Player phylactery) {
    this.phylactery = phylactery;
  }

  @Override
  public String getDayKillNotice() {
    return "Demon";
  }

  @Override
  public String getNightKillNotice() {
    return "Demon";
  }

  @Override
  public String diedAtNightNotice() {
    return " died in the night and was the Demon.";
  }

  @Override
  protected void onNightBegins() {
    super.onNightBegins();
    getEngine().getBot().sendMessage(getPlayer(),
        "Tell me who you want to kill.  Message me '!kill [target]'");
  }

  @Override
  public boolean isProtected() {
    return phylactery.isAlive();
  }

  @Override
  public Faction getFaction() {
    return Faction.NEUTRAL;
  }

  @Override
  public boolean isFinished() {
    if (isNight() && currentKillTarget == null) {
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

  @Override
  public void end(Time time, Collection<GameRole> demons) {
    if (time == Time.Night) {
      Player toKill = currentKillTarget;
      KillSpell spell = new KillSpell(toKill);
      String message = toKill.getName() + " was a " + toKill.getRole().getNightKillNotice() + ".";
      for (GameRole demon : demons) {
        spell.getMessages().put(demon.getPlayer(), message);
      }
      getEngine().cast(spell);
    }
    super.end(time, demons);
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
