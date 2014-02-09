package wolf.model.stage;

import java.util.List;

import com.google.common.collect.ImmutableList;
import wolf.action.Action;
import wolf.action.GetHelpAction;
import wolf.action.setup.InitGameAction;
import wolf.bot.IBot;
import wolf.model.Player;

public class InitialStage extends Stage {

  private final List<Action> actions = ImmutableList.<Action>of(new InitGameAction(this),
      new GetHelpAction(this));

  public InitialStage(IBot bot) {
    super(bot);
  }
  
  @Override
  public List<Action> getAvailableActions(Player player) {
    return actions;
  }

  @Override
  public Iterable<Player> getAllPlayers() {
    return ImmutableList.of();
  }

}
