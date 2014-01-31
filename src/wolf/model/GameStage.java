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

}
