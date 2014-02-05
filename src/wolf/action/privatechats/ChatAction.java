package wolf.action.privatechats;

import java.util.List;

import wolf.model.Player;
import wolf.model.chat.ChatServer;

import com.google.common.base.Joiner;

public class ChatAction extends PrivateChatAction {

  public ChatAction(ChatServer server) {
    super(server, "chat", "message");
  }

  @Override
  protected void execute(Player invoker, List<String> args) {
    StringBuilder output = new StringBuilder();
    output.append(Joiner.on(" ").join(args));
    getServer().sendMessage(invoker.getName(), output.toString());
  }


  @Override
  public String getDescription() {
    return "Send a message to your private chat room.";
  }

  @Override
  protected boolean argSizeMatters() {
    return false;
  }

}
