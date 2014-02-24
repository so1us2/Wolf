package wolf.web;

import java.awt.Toolkit;
import java.util.List;
import java.util.Map;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import wolf.web.LoginService.User;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameRouter extends BaseWebSocketHandler {

  private final Map<WebSocketConnection, ConnectionInfo> connectionInfo = Maps.newConcurrentMap();

  private final JsonParser parser = new JsonParser();

  private final LoginService loginService = new LoginService();

  private List<GameRoom> rooms = Lists.newCopyOnWriteArrayList();

  public GameRouter() {
    rooms.add(new GameRoom());
  }

  @Override
  public void onOpen(WebSocketConnection connection) {
    ConnectionInfo info = new ConnectionInfo(connection);

    connectionInfo.put(connection, info);

    rooms.get(0).onJoin(info); // auto-join main room
  }

  @Override
  public void onClose(WebSocketConnection connection) {
    ConnectionInfo removed = connectionInfo.remove(connection);

    GameRoom room = removed.getRoom();
    if (room != null) {
      if (room.onLeave(removed)) {
        rooms.remove(room);
        announceRooms();
      }
    }
  }

  public void createRoom(String name) {
    System.out.println("CREATE ROOM: " + name);

    for (GameRoom room : rooms) {
      if (room.name.equalsIgnoreCase(name)) {
        throw new RuntimeException("Already a room with this name: " + name);
      }
    }
    
    GameRoom room = new GameRoom(name);
    rooms.add(room);
    announceRooms();
  }

  @Override
  public void onMessage(WebSocketConnection connection, String message) {
    message = message.trim();
    if (message.isEmpty()) {
      System.out.println("Received empty message.");
      return;
    }

    ConnectionInfo info = connectionInfo.get(connection);

    System.out.println(info + ": " + message);

    JsonObject o = parser.parse(message).getAsJsonObject();

    String command = o.get("command").getAsString();
    List<String> args = Lists.newArrayList();
    for(JsonElement e : o.get("args").getAsJsonArray()){
      args.add(e.getAsString());
    }

    handle(info, command, args);
  }

  private void handle(ConnectionInfo from, String command, List<String> args) {
    if (command.equalsIgnoreCase("chat")) {
      String sender = from.getName();
      if (sender == null) {
        from.send("You must login before chatting!");
        return;
      }

      // TODO pbigelow logged in from: /65.130.25.203:49733
      if (sender.equalsIgnoreCase("oscar") || sender.equalsIgnoreCase("wwkaye")
          || sender.equalsIgnoreCase("tony") || sender.equalsIgnoreCase("ray56")
          || sender.equalsIgnoreCase("pbigelow")) {
        System.out.println("BANNED!");
        from.send(constructChatJson(GameRoom.NARRATOR, "You are banned."));
        return;
      }

      GameRoom room = from.getRoom();
      if (room != null) {
        room.handleChat(sender, args.get(0));
      }
    } else if (command.equalsIgnoreCase("login")) {
      long userID = Long.parseLong(args.get(0));
      from.setUserID(userID);

      User user = loginService.handleLogin(userID);

      if (user == null || user.name == null) {
        from.send(constructJson("PROMPT_NAME"));
        return;
      }

      String ip = from.getConnection().httpRequest().remoteAddress() + "";
      System.out.println(user.name + " logged in from: " + ip);

      if (ip.contains("65.130.25.203")) {
        Toolkit.getDefaultToolkit().beep();
        System.err.println("OSCAR ALERT :: " + user.name);
      }

      from.setName(user.name);

      from.send(constructJson("LOGIN_SUCCESS", "username", user.name, "enable_sounds",
          user.enableSounds));

      from.getRoom().onPlayersChanged();
    } else if (command.equalsIgnoreCase("username")) {
      long userID = from.getUserID();
      String name = args.get(0);

      if (name.contains("<") || name.contains(">")) {
        from.send(constructChatJson(GameRoom.NARRATOR, "Invalid username."));
        return;
      }

      System.out.println(name + " created an account from: "
          + from.getConnection().httpRequest().remoteAddress());

      loginService.createAccount(userID, name);

      from.setName(name);

      from.send(constructJson("LOGIN_SUCCESS", "username", name));

      from.getRoom().onPlayersChanged();
    } else if (command.equalsIgnoreCase("switch_room")) {
      String room = args.get(0);
      GameRoom newRoom = getRoom(room);
      GameRoom oldRoom = from.getRoom();
      if (newRoom == oldRoom || newRoom == null) {
        return;
      }
      if (oldRoom != null && oldRoom.onLeave(from)) {
        rooms.remove(oldRoom);
        announceRooms();
      }
      newRoom.onJoin(from);
    }
  }

  public static String constructChatJson(String from, String message) {
    return GameRouter.constructJson("CHAT", "from", from, "msg", message);
  }

  public static String constructJson(String command, Object... params) {
    JsonObject o = new JsonObject();

    o.addProperty("command", command);

    for (int i = 0; i < params.length; i += 2) {
      String key = params[i].toString();
      Object val = params[i + 1];
      if (val instanceof Boolean) {
        o.addProperty(key, (Boolean) val);
      } else if (val instanceof JsonElement) {
        o.add(key, (JsonElement) val);
      } else {
        o.addProperty(key, val.toString());
      }
    }

    return o.toString();
  }

  private void announceRooms() {
    String s = constructJson("LOAD_ROOMS");

    for (ConnectionInfo info : ImmutableList.copyOf(connectionInfo.values())) {
      info.send(s);
    }
  }

  public GameRoom getRoom(String name) {
    for (GameRoom room : rooms) {
      if (room.name.equalsIgnoreCase(name)) {
        return room;
      }
    }
    return null;
  }

  public List<GameRoom> getRooms() {
    return ImmutableList.copyOf(rooms);
  }

}
