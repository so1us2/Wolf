package wolf.action.privatechats;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import wolf.WolfException;
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

  /**
   * Override apply to ignore the # of arguments issue.
   */
  @Override
  public void apply(Player invoker, List<String> args, boolean isPrivate) {
    checkNotNull(invoker);
    checkNotNull(args);
    if (requiresAdmin() && !invoker.isAdmin()) {
      throw new WolfException("You must be an admin to do that.");
    }
    execute(invoker, args);
  }

  @Override
  public String getDescription() {
    return "Send a message to your private chat room.";
  }

}
