package wolf.model.chat;

import java.util.List;
import java.util.Set;

import wolf.model.Player;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class ChatRoom {

  Set<Player> members;
  ChatServer server;
  String roomName;

  public ChatRoom(ChatServer server, String name) {
    members = Sets.newTreeSet();
    this.server = server;
    roomName = name;
  }
  
  public ChatRoom(ChatServer server, String name, List<Player> founders) {
    this(server, name);
    for (Player p : founders) {
      members.add(p);
    }
    sendMessage(roomName + " is created.");
    sendMessage("Players here: " + Joiner.on(", ").join(members));
  }

  public String getName() {
    return roomName;
  }

  public Set<Player> getMembers() {
    return members;
  }

  public boolean contains(Player p) {
    return members.contains(p);
  }

  public boolean isEmpty() {
    return members.isEmpty();
  }

  public void join(Player p) {
    members.add(p);
    sendMessage(p.getName() + " has joined the room.");
    server.getBot().sendMessage(
        p.getName() + " is in " + roomName + " with: " + Joiner.on(",").join(members));
  }

  public void leave(Player p) {
    members.remove(p);
    if (members.isEmpty()) {
      server.closeRoom(this);
    } else {
      sendMessage(p.getName() + " has left the room.");
    }
    server.getBot().sendMessage(p.getName() + " has left " + roomName);
  }

  public void sendMessage(Player sender, String message) {
    assert (members.contains(sender));

    for (Player p : members) {
      server.getBot().sendMessage(p.getName(), "<PC> " + sender.getName() + ": " + message);
    }
  }

  public void sendMessage(String message) {
    for (Player p : members) {
      server.getBot().sendMessage(p.getName(), "<PC> " + message);
    }
  }
}
