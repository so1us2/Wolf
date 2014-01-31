package wolf.action.game;

import java.util.List;
import java.util.Map;

import org.jibble.pircbot.User;

import wolf.WolfBot;
import wolf.WolfException;
import wolf.engine.Player;

public class ReplaceAction extends AbstractGameAction {

  public ReplaceAction() {
    super(2);
  }

  @Override
  public String getCommandName() {
    return "replace";
  }

  @Override
  protected void execute(WolfBot bot, String sender, String command, List<String> args) {
    String fromPlayer = args.get(0);
    String toPlayer = args.get(1);

    if (engine.getPlayer(toPlayer) != null) {
      throw new WolfException(toPlayer + " is already in the game.");
    }

    User toUser = bot.getUser(toPlayer);
    if (toUser == null) {
      throw new WolfException("Could not find player: " + toPlayer);
    }

    Map<String, Player> map = engine.getNamePlayerMap();
    Player player = map.remove(fromPlayer.toLowerCase());

    if (player == null) {
      throw new WolfException(fromPlayer + " not in game.");
    }

    Player newPlayer = new Player(toPlayer);
    newPlayer.setRole(player.getRole());

    map.put(toPlayer.toLowerCase(), newPlayer);

    bot.deVoice(WolfBot.channel, fromPlayer);
    bot.deOp(WolfBot.channel, toPlayer);
    bot.voice(WolfBot.channel, toPlayer);

    bot.sendMessage(toPlayer,
        "You have replaced " + fromPlayer + ". You are a " + newPlayer.getRole() + ".");
    // If player is taking over a role that has info, need to share that info.
    newPlayer.getRole().sendHistory();

    bot.sendMessage(toPlayer + " has replaced " + fromPlayer + ".");
  }

}
