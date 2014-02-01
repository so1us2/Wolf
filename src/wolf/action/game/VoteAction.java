package wolf.action.game;

import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;
import wolf.WolfException;
import wolf.action.Visibility;
import wolf.model.Player;
import wolf.model.stage.GameStage;

public class VoteAction extends GameAction {

  public VoteAction(GameStage stage) {
    super(stage, "vote", "player");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    Player target = getStage().getPlayer(args.get(0));
    Map<Player, Player> votes = getStage().getVotesToLynch();

    Player prevTarget = votes.put(invoker, target);

    if (Objects.equal(target, prevTarget)) {
      throw new WolfException("Your vote was already set to " + target.getName());
    }

    getStage().getVotingHistory().record(invoker, target);

    if (prevTarget == null) {
      getBot().sendMessage(invoker.getName(), "Voted for " + target.getName());
      getBot().sendMessage("A player voted. (" + votes.size() + " total)");
    } else {
      getBot().sendMessage(invoker.getName(), "Switched vote to " + target.getName());
      getBot().sendMessage("A player switched their vote. (" + votes.size() + " total)");
    }

    if (votes.size() == getStage().getPlayers().size()) {
      processVotes(votes);
    }
  }

  /**
   * Checks for a majority -- and if there is one, performs a lynching.
   */
  private void processVotes(Map<Player, Player> votes) {

  }

  @Override
  public String getDescription() {
    return "Votes to have a player lynched.";
  }

  @Override
  public Visibility getVisibility() {
    return Visibility.PRIVATE;
  }

}
