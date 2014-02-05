package wolf.action.privatechats;

import java.util.List;

import wolf.model.Player;
import wolf.model.chat.ChatServer;

public class NewRoomAction extends PrivateChatAction {

  public NewRoomAction(ChatServer server) {
    super(server, "newroom", "roomname");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getServer().startRoom(invoker.getName(), args.get(0));
  }

  @Override
  public String getDescription() {
    return "Create a new private room.";
  }

}
