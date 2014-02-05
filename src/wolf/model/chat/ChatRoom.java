package wolf.model.chat;

import java.util.List;
import java.util.Set;

import wolf.WolfException;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class ChatRoom {

  private Set<String> members;
  private final ChatServer server;
  private final String roomName;
  private Set<String> authorized;

  public ChatRoom(ChatServer server, String name) {
    members = Sets.newTreeSet();
    authorized = Sets.newTreeSet();
    this.server = server;
    roomName = name;
  }
  
  public ChatRoom(ChatServer server, String name, List<String> founders) {
    this(server, name);
    for (String s : founders) {
      members.add(s);
    }
    sendMessage(roomName + " is created.");
    sendMessage("Players here: " + Joiner.on(", ").join(members));
  }

  public String getName() {
    return roomName;
  }

  public Set<String> getMembers() {
    return members;
  }

  public boolean contains(String p) {
    return members.contains(p);
  }

  public boolean isEmpty() {
    return members.isEmpty();
  }

  /**
   * This method should only be called by the server as the server needs to ensure a player leaves a previous room before joining.
   */
  protected void join(String player) {
    if (contains(player)) {
      return;
    }
    if (!authorized.contains(player)) {
      sendMessage(player
          + " attempted to join but is not authorized. !authorize <name> to permit entry.");
      throw new WolfException("You are not authorized to join " + roomName);
    }
    sendMessage(player + " has joined the room.");
    server.getBot().sendMessage(
        player + " is in " + roomName + " with: " + Joiner.on(", ").join(members));
    members.add(player);
  }

  public void authorizePlayer(String p) {
    authorized.add(p);
    sendMessage(p + " is authorized to join.");
  }

  public void deauthorizePlayer(String player) {
    authorized.remove(player);
    sendMessage(player + " is no longer authorized to join.");
  }

  public void leave(String player) {
    members.remove(player);
    if (members.isEmpty()) {
      server.closeRoom(this);
    } else {
      sendMessage(player + " has left the room.");
    }
    server.getBot().sendMessage(player + " has left " + roomName);
  }

  /**
   * This is for messages from players to the room.
   * @param sender
   * @param message
   */
  public void sendMessage(String sender, String message) {
    assert (members.contains(sender));

    for (String player : members) {
      server.getBot().sendMessage(player, "<" + roomName + "> " + sender + ": " + message);
    }
  }

  /**
   * This is for messages from the server to the room.
   * @param message
   */
  public void sendMessage(String message) {
    for (String player : members) {
      server.getBot().sendMessage(player, "<" + roomName + "> " + message);
    }
  }
}
