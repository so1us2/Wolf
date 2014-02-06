package wolf.action.privatechats;

import java.util.List;

import wolf.WolfException;
import wolf.model.Player;
import wolf.model.chat.ChatRoom;
import wolf.model.chat.ChatServer;

public class LeaveRoomAction extends PrivateChatAction {

  public LeaveRoomAction(ChatServer server) {
    super(server, "leaveroom", "roomname");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    ChatRoom room = getServer().findRoomForPlayer(invoker.getName());
    if (room == null) {
      throw new WolfException(invoker.getName() + " is not in a room.");
    }
    room.leave(invoker.getName());
  }

  @Override
  public String getDescription() {
    return "Leave a private chat.";
  }

}
