package wolf.model;

import java.util.List;

import wolf.action.Action;
import wolf.action.setup.InitGameAction;

import com.google.common.collect.ImmutableList;

public class InitialStage extends Stage {

  private static final List<Action> actions = ImmutableList.<Action> of(new InitGameAction());
  
  @Override
  public List<Action> getAvailableActions() {
    return actions;
  }

}
