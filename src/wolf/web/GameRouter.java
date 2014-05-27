package wolf.web;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import wolf.model.stage.GameStage;
import wolf.web.LoginService.User;

import com.beust.jcommander.internal.Sets;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameRouter extends BaseWebSocketHandler {

  public static final Set<String> banned = Sets.newHashSet();
  static {
    // banned.add("oscar");
    banned.add("wwkaye");
    banned.add("tony");
    banned.add("ray56");
    banned.add("pbigelow");
    banned.add("livermush");// /174.130.134.155:59992
    // banned.add("todd");

    // TODO pbigelow logged in from: /65.130.25.203:49733
    // TODO oscar logged in from /71.219.0.137:57998
  }

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

    int numActiveGames = 0;
    for (GameRoom room : rooms) {
      if (room.getStage() instanceof GameStage) {
        numActiveGames++;
      }
    }
    GameRoom mainRoom = rooms.get(0);
    if (numActiveGames > 1) {
      connection.send(constructChatJson(GameRoom.NARRATOR,
          "<b>There are multiple games going on at once. "
              + "Use the dropdown to switch to another room.</b>"));
    } else if (numActiveGames == 1 && !(mainRoom.getStage() instanceof GameStage)) {
      connection.send(constructChatJson(GameRoom.NARRATOR,
          "<b>There is a game going on in another room. "
              + "Use the dropdown menu if you want to switch rooms.</b>"));
    }

    mainRoom.onJoin(info); // auto-join main room
  }

  @Override
  public void onClose(WebSocketConnection connection) {
    ConnectionInfo removed = connectionInfo.remove(connection);

    GameRoom room = removed.getRoom();
    if (room == null) {
      System.err.println(removed.getName() + " was not in a room when they DC'd!");
      return;
    }

    if (room.onLeave(removed)) {
      rooms.remove(room);
      announceRooms();
    }
  }

  public void createRoom(String name) {
    try {
      name = URLDecoder.decode(name, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }

    if (!StringUtils.isAlphanumeric(name.replace(" ", ""))) {
      throw new RuntimeException("Invalid room name: " + name);
    }

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

      if (banned.contains(sender.toLowerCase())) {
        System.out.println(banned + " is BANNED");
        from.send(constructChatJson(GameRoom.NARRATOR, "You are banned."));
        return;
      }

      GameRoom room = from.getRoom();
      if (room != null) {
        room.handleChat(sender, args.get(0));
      }
    } else if (command.equalsIgnoreCase("login")) {
      long userID = Long.parseLong(args.get(0));
      String accessToken = args.get(1);

      String facebookURL =
          "https://graph.facebook.com/debug_token?input_token=" + accessToken
              + "&access_token=265390563624958|9cef6b505981506f7ca882d16cfb1b58";

      JsonObject o;
      try {
        o = parser.parse(Resources.toString(new URL(facebookURL), Charsets.UTF_8))
                .getAsJsonObject().getAsJsonObject("data");
      } catch (Exception e) {
        throw Throwables.propagate(e);
      }

      long fbUserID = o.get("user_id").getAsLong();
      boolean isValid = o.get("is_valid").getAsBoolean();

      if (!isValid) {
        JsonObject error = o.getAsJsonObject("error");
        String message = error.get("message").getAsString();
        if (message.contains("has expired") || message.contains("logged out")
            || message.contains("Invalid facebook login")) {
          isValid = true;
        }
      }

      if (userID != fbUserID) {
        System.err.println(o);
        throw new RuntimeException("userID doesn't match the facebook userID! " + userID + " vs "
            + fbUserID);
      }

      if (!isValid) {
        System.err.println(o);
        throw new RuntimeException("Invalid facebook login.");
      }

      from.setUserID(userID);

      User user = loginService.handleLogin(userID);

      if (user == null || user.name == null) {
        from.send(constructJson("PROMPT_NAME"));
        return;
      }

      String ip = from.getConnection().httpRequest().remoteAddress() + "";
      System.out.println(user.name + " logged in from: " + ip);

      from.setName(user.name);

      from.send(constructJson("LOGIN_SUCCESS", "username", user.name, "enable_sounds",
          user.enableSounds));

      from.getRoom().onPlayersChanged();
    } else if (command.equalsIgnoreCase("username")) {
      long userID = from.getUserID();
      String name = args.get(0);

      if (!StringUtils.isAlphanumeric(name)) {
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
      // if (true) {
      // from.send(constructChatJson(GameRoom.NARRATOR,
      // "Switching rooms has been disabled until server downtime has completed."));
      // return;
      // }

      String room = args.get(0);
      GameRoom newRoom = getRoom(room);
      GameRoom oldRoom = from.getRoom();

      if (newRoom == null) {
        String msg = "Room does not exist: " + room;
        System.out.println(msg);
        from.send(constructJson("SWITCH_ROOM", "room", oldRoom.name, "msg", msg));
        return;
      }

      if (oldRoom == newRoom) {
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
