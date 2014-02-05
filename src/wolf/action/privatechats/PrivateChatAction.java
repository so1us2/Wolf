package wolf.action.privatechats;

import wolf.action.Action;
import wolf.model.chat.ChatServer;

public abstract class PrivateChatAction extends Action {

  private final ChatServer server;

  public PrivateChatAction(ChatServer server, String name, String... argsNames) {
    super(name, argsNames);
    this.server = server;
  }

  public ChatServer getServer() {
    return server;
  }

}
