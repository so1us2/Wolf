package wolf.action.privatechats;

import java.util.List;

import wolf.model.Player;
import wolf.model.chat.ChatServer;

public class JoinRoomAction extends PrivateChatAction {

  public JoinRoomAction(ChatServer server) {
    super(server, "joinroom", "roomname");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    getServer().joinRoom(invoker.getName(), args.get(0));
  }

  @Override
  public String getDescription() {
    return "Join a specified private chat";
  }

}
