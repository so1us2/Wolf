package wolf.model;

import java.util.List;

import com.google.common.collect.ImmutableList;
import wolf.action.Action;
import wolf.action.setup.CommandsAction;
import wolf.action.setup.InitGameAction;
import wolf.bot.IBot;

public class InitialStage extends Stage {

  private final List<Action> actions = ImmutableList.<Action>of(new InitGameAction(this),
      new CommandsAction(this));

  public InitialStage(IBot bot) {
    super(bot);
  }
  
  @Override
  public List<Action> getAvailableActions() {
    return actions;
  }

}
