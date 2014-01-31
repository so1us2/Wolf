package wolf.model;

import java.util.List;

import wolf.action.Action;

public abstract class Stage {

  public void handle(GameModel model, String sender, String command, List<String> args, boolean isPrivate) {

  }

  public abstract List<Action> getAvailableActions();

}
