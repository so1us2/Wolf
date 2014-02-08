package wolf.model.chat;

import java.util.Set;

import wolf.WolfException;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

public class ChatRoom {

  private final Set<String> members;
  private final ChatServer server;
  private final String roomName;
  private final Set<String> authorized;

  public ChatRoom(ChatServer server, String name, String founder) {
    members = Sets.newTreeSet();
    authorized = Sets.newTreeSet();
    this.server = server;
    roomName = name.toUpperCase();
    members.add(founder);
    sendMessageToRoom(roomName + " is created.");
    sendMessageToRoom("Players here: " + Joiner.on(", ").join(members));
  }

  public String getName() {
    return roomName;
  }

  public Set<String> getMembers() {
    return ImmutableSet.copyOf(members);
  }

  public boolean contains(String p) {
    return members.contains(p);
  }

  /**
   * This method should only be called by the server as the server needs 
   * to ensure a player leaves a previous room before joining.
   */
  protected void join(String player) {
    if (contains(player)) {
      return;
    }
    if (!authorized.contains(player)) {
      sendMessageToRoom(player
          + " attempted to join but is not authorized. !authorize <name> to permit entry.");
      throw new WolfException("You are not authorized to join " + roomName);
    }
    sendMessageToRoom(player + " has joined the room.");
    StringBuilder output = new StringBuilder();
    output.append(player).append(" is in ").append(roomName);
    if (members.size() > 1) {
      output.append(" with: ").append(Joiner.on(", ").join(members));
    }
    output.append(".");
    server.getBot().sendMessage(output.toString());
    members.add(player);
  }

  public void authorizePlayer(String p) {
    authorized.add(p);
    sendMessageToRoom(p + " is authorized to join.");
  }

  public void deauthorizePlayer(String player) {
    authorized.remove(player);
    sendMessageToRoom(player + " is no longer authorized to join.");
  }

  public void leave(String player) {
    members.remove(player);
    if (members.isEmpty()) {
      server.closeRoom(this);
    } else {
      sendMessageToRoom(player + " has left the room.");
    }
    server.getBot().sendMessage(player + " has left " + roomName + ".");
  }

  /**
   * This is for messages from players to the room.
   */
  public void sendMessageToRoom(String sender, String message) {
    assert (members.contains(sender));

    for (String player : members) {
      if (!player.equals(sender)) {
      server.getBot().sendMessage(player, "<" + roomName + "> " + sender + ": " + message);
      }
    }
  }

  /**
   * This is for messages from the server to the room.
   */
  public void sendMessageToRoom(String message) {
    for (String player : members) {
      server.getBot().sendMessage(player, "<" + roomName + "> " + message);
    }
  }
}
