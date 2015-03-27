package wolf.model.stage;

import java.util.List;
import wolf.action.Action;
import wolf.action.setup.NewGameAction;
import wolf.bot.IBot;
import wolf.model.GameConfig;
import wolf.model.Player;
import com.google.common.collect.ImmutableList;

public class InitialStage extends Stage {

  private final List<Action> actions = ImmutableList.<Action>of(new NewGameAction(this));

  public InitialStage(IBot bot) {
    super(bot);

    bot.unmuteAll();
    getBot().onPlayersChanged();
  }
  
  @Override
  public GameConfig getConfig() {
    return null;
  }

  @Override
  public List<Action> getStageActions(Player player) {
    return actions;
  }

  @Override
  public Iterable<Player> getAllPlayers() {
    return ImmutableList.of();
  }

  @Override
  public Player getPlayerOrNull(String name) {
    return null;
  }

  @Override
  public int getStageIndex() {
    return 0;
  }

}
