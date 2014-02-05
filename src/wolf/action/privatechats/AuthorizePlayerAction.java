package wolf.action.privatechats;

import java.util.List;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.chat.ChatRoom;
import wolf.model.chat.ChatServer;

public class AuthorizePlayerAction extends PrivateChatAction {

  public AuthorizePlayerAction(ChatServer server) {
    super(server, "authorize", "player");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    ChatRoom room = getServer().findRoomForPlayer(invoker.getName());
    if (room == null) {
      throw new WolfException(invoker.getName() + " is not in a room.");
    }
    room.authorizePlayer(args.get(0));
  }

  @Override
  public String getDescription() {
    return "Authorize a player to join your private room.";
  }

}
