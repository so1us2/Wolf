package wolf.action.setup;

import java.util.List;

import com.google.common.collect.ImmutableSortedSet;
import wolf.model.Player;
import wolf.model.stage.SetupStage;

public class InviteAction extends SetupAction {

  public InviteAction(SetupStage stage) {
    super(stage, "invite", "player");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    String target = args.get(0);

    getStage().getInviteList().add(target.toLowerCase());

    getBot().sendMessage(target,
        invoker.getName() + " has invited you to join their game!" +
            " Use /join to play with them.");

    getBot().sendMessage(invoker.getName(),
        "Sent a message to " + target + " inviting them to join!");
    getBot().sendMessage(invoker.getName(), "You've invited these people: "
        + ImmutableSortedSet.copyOf(getStage().getInviteList()));
  }

  @Override
  public String getDescription() {
    return "Invite a player to join your game.";
  }

  @Override
  protected boolean requiresHost() {
    return true;
  }

}
