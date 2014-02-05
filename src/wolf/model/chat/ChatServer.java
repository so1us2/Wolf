package wolf.model.chat;

import java.util.List;

import org.testng.collections.Lists;

import wolf.WolfException;
import wolf.bot.IBot;

import com.google.common.base.Joiner;

/**
 * Private chat server that announces who is talking with whom to the public chat so people know.
 * May want to move to its on PircBot as well so it can be in a separate window from game actions in IRC.
 * @author Tom
 */
public class ChatServer {

  private IBot bot;
  private List<ChatRoom> rooms;

  public ChatServer(IBot bot) {
    this.bot = bot;
    rooms = Lists.newArrayList();
  }

  public ChatRoom findRoomForPlayer(String sender) {
    for (ChatRoom room : rooms) {
      if (room.contains(sender)) {
        return room;
      }
    }
    return null;
  }

  public ChatRoom findRoomByName(String name) {
    for (ChatRoom room : rooms) {
      if (room.getName().equalsIgnoreCase(name)) {
        return room;
      }
    }
    return null;
  }

  public IBot getBot() {
    return bot;
  }

  public void sendMessage(String sender, String message) {
    ChatRoom room = findRoomForPlayer(sender);
    if (room == null) {
      throw new WolfException("You are not in a room.");
    }
    room.sendMessage(sender, message);
  }

  public void startRoom(String founder, String name) {
    if (findRoomByName(name) != null) {
      throw new WolfException("A room by that name already exists.");
    }
    ChatRoom newRoom = new ChatRoom(this, name);
    newRoom.authorizePlayer(founder);
    rooms.add(newRoom);
    joinRoom(founder, name);
  }

  public void closeRoom(ChatRoom room) {
    rooms.remove(room);
  }

  public void joinRoom(String player, String roomName) {
    ChatRoom room = findRoomByName(roomName);
    if (room == null) {
      throw new WolfException("There is no room named " + roomName);
    }

    // Currently if a player fails to join a new room, they still leave their old. Not sure this is
    // best behavior.
    ChatRoom oldRoom = findRoomForPlayer(player);
    if (oldRoom != null) {
      oldRoom.leave(player);
    }
    room.join(player);
  }

  public void listRooms() {
    if (rooms.isEmpty()) {
      bot.sendMessage("There are currently no rooms.");
    } else {
      for (ChatRoom r : rooms) {
        bot.sendMessage(r.getName() + ": " + Joiner.on(",").join(r.getMembers()));
      }
    }
  }

  public void clearAllRooms() {
    rooms.clear();
  }

}
