package wolf.model;

import java.util.List;

import wolf.action.Action;
import wolf.action.setup.InitGameAction;
import wolf.bot.NarratorBot;

import com.google.common.collect.ImmutableList;

public class InitialStage extends Stage {

  private final List<Action> actions = ImmutableList.<Action> of(new InitGameAction(this));

  public InitialStage(NarratorBot bot) {
    super(bot);
  }
  
  @Override
  public List<Action> getAvailableActions() {
    return actions;
  }

}
