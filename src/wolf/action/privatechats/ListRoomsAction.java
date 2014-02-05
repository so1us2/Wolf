package wolf.action.privatechats;

import java.util.List;

import wolf.action.Visibility;
import wolf.model.Player;
import wolf.model.chat.ChatServer;

public class ListRoomsAction extends PrivateChatAction {

  public ListRoomsAction(ChatServer server) {
    super(server, "listrooms");
  }

  // Why isn't execute passed isPrivate? Wouldn't that be useful to have?
  @Override
  protected void execute(Player invoker, List<String> args) {
    getServer().listRooms();
  }

  @Override
  public String getDescription() {
    return "List all rooms.";
  }

  @Override
  public Visibility getVisibility() {
    return Visibility.BOTH;
  }

}
