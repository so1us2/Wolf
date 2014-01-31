package wolf.model;

import java.util.List;

import wolf.action.Action;
import wolf.bot.NarratorBot;

import com.google.common.collect.ImmutableList;

public class GameStage extends Stage {

  public GameStage(NarratorBot bot) {
    super(bot);
  }

  @Override
  public List<Action> getAvailableActions() {
    return ImmutableList.of();
  }

  // private void assignRoles(Map<Class<? extends Role>, Integer> roleCountMap) throws Exception {
  // List<Player> playersWhoNeedRoles = Lists.newArrayList(namePlayerMap.values());
  // outerLoop: for (Entry<Class<? extends Game>, Integer> entry : roleCountMap.entrySet()) {
  // for (int i = 0; i < entry.getValue(); i++) {
  // if (playersWhoNeedRoles.isEmpty()) {
  // break outerLoop;
  // }
  // Player randomPlayer =
  // playersWhoNeedRoles.remove((int) (Math.random() * playersWhoNeedRoles.size()));
  // Role role = entry.getKey().newInstance();
  // randomPlayer.setRole(role);
  // }
  // }
  //
  // bot.sendMessage("Assigning roles...");
  //
  // for (Player player : namePlayerMap.values()) {
  // bot.sendMessage(player, "You are a " + player.getRole() + ".");
  // }
  //
  // bot.sendMessage("Roles have been assigned.");
  // }

}
