package wolf.model.role;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import wolf.action.Action;
import wolf.bot.IBot;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.Role;
import wolf.model.stage.GameStage;

import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableList;

public abstract class AbstractRole implements Comparable<AbstractRole> {

  public static final BiMap<Role, Class<? extends AbstractRole>> roleMap = HashBiMap.create();

  static {
    roleMap.put(Role.VILLAGER, Villager.class);
    roleMap.put(Role.WOLF, Wolf.class);
    roleMap.put(Role.SEER, Seer.class);
    roleMap.put(Role.PRIEST, Priest.class);
    roleMap.put(Role.VIGILANTE, Vigilante.class);
    roleMap.put(Role.BARTENDER, Bartender.class);
    roleMap.put(Role.HUNTER, Hunter.class);
    roleMap.put(Role.MINION, Minion.class);
    roleMap.put(Role.DEMON, Demon.class);
  }

  private final Role role;
  private GameStage stage;
  private Player player;

  public AbstractRole() {
    this.role = roleMap.inverse().get(getClass());
  }

  public Role getType() {
    return role;
  }

  public Faction getFaction() {
    return role.getFaction();
  }

  public Faction getVictoryTeamFaction() {
    return role.getFaction();
  }

  public Player getTarget() {
    return null;
  }

  public void onGameStart() {}

  public void onNightBegins() {}

  public void onNightEnds() {}

  /**
  * This method is called when a new player is replacing someone that had to leave.
  * Subclasses can override this to give the new player relevant information that
  * he/she might need to fulfill the duties of their role. 
  */
  public void onPlayerSwitch() {
    getStage().getBot().sendMessage(player.getName(), "Welcome to the game. You are a " + role);
  }

  public abstract String getDescription();

  @Override
  public String toString() {
    return role.toString();
  }

  public void setStage(GameStage stage) {
    this.stage = checkNotNull(stage);
  }

  public GameStage getStage() {
    return stage;
  }

  public void setPlayer(Player player) {
    this.player = player;
  }

  public Player getPlayer() {
    return player;
  }

  public IBot getBot() {
    return stage.getBot();
  }

  public boolean isFinishedWithNightAction() {
    return true;
  }

  public List<Action> getNightActions() {
    return ImmutableList.of();
  }

  public void handleChat(Player sender, String message, boolean isPrivate) {}

  public static AbstractRole create(Role role, Player player) {
    Class<? extends AbstractRole> c = roleMap.get(role);
    checkNotNull(c, "No implementation mapped for role: " + role);
    try {
      AbstractRole ret = c.newInstance();
      ret.setPlayer(player);
      return ret;
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public int compareTo(AbstractRole other) {
    return getType().name().compareTo(other.getType().name());
  }
}
