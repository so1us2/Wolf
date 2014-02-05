package wolf.model.chat;

import java.util.List;

import org.testng.collections.Lists;

import wolf.WolfException;
import wolf.bot.IBot;
import wolf.model.Player;

import com.google.common.base.Joiner;

/**
 * Private chat server that announces who is talking with whom to the public chat so people know.
 * TODOS: Need way to control entry. Needs some kind of invite / acceptance model.
 * @author Tom
 */
public class ChatServer {

  private IBot bot;
  private List<ChatRoom> rooms;

  public ChatServer(IBot bot) {
    this.bot = bot;
    rooms = Lists.newArrayList();
  }

  private ChatRoom findRoomForPlayer(Player sender) {
    for (ChatRoom room : rooms) {
      if (room.contains(sender)) {
        return room;
      }
    }
    return null;
  }

  private ChatRoom findRoomByName(String name) {
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

  public void sendMessage(Player sender, String message) {
    ChatRoom room = findRoomForPlayer(sender);
    if (room == null) {
      throw new WolfException(sender.getName() + " is not in a room.");
    }
    room.sendMessage(sender, message);
  }

  public void startRoom(List<Player> founders, String name) {
    if (findRoomByName(name) != null) {
      throw new WolfException("A room by that name already exists.");
    }
    ChatRoom newRoom = new ChatRoom(this, name, founders);
    rooms.add(newRoom);
  }

  public void closeRoom(ChatRoom room) {
    rooms.remove(room);
  }

  public void joinRoom(Player p, String name) {
    ChatRoom room = findRoomByName(name);
    if (room == null) {
      throw new WolfException("There is no room named " + name);
    }
    ChatRoom oldRoom = findRoomForPlayer(p);
    if (oldRoom != null) {
      oldRoom.leave(p);
    }
    room.join(p);
  }

  public void listRooms() {
    for (ChatRoom r : rooms) {
      bot.sendMessage(r.getName() + ": " + Joiner.on(",").join(r.getMembers()));
    }
  }

}
