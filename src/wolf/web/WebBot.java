package wolf.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

import wolf.WolfException;
import wolf.bot.IBot;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.stage.GameStage;
import wolf.model.stage.InitialStage;
import wolf.model.stage.Stage;
import wolf.rankings.GameHistory;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class WebBot extends BaseWebSocketHandler implements IBot {

  private final Map<WebSocketConnection, String> connectionNameMap = Maps.newConcurrentMap();
  private final Map<String, WebSocketConnection> nameConnectionMap = Maps.newConcurrentMap();
  private final List<WebSocketConnection> allConnections = Lists.newArrayList();

  private final Map<WebSocketConnection, Long> connectionIds = Maps.newConcurrentMap();

  private final JsonParser parser = new JsonParser();

  private Stage stage = new InitialStage(this);

  private boolean moderated = false;
  private Set<String> playersAllowedToSpeak = Sets.newHashSet();
  
  private final GameHistory history = new GameHistory();

  private final LoginService loginService = new LoginService();

  @Override
  public void onOpen(WebSocketConnection connection) {
    allConnections.add(connection);
    connection.send(createPlayersObject());
  }

  @Override
  public void onClose(WebSocketConnection connection) {
    allConnections.remove(connection);
    connectionIds.remove(connection);
    String user = connectionNameMap.remove(connection);
    if (user != null) {
      nameConnectionMap.remove(user);
    }
    sendRemote(createPlayersObject());
  }

  @Override
  public void onMessage(WebSocketConnection connection, String message) {
    message = message.trim();
    if (message.isEmpty()) {
      System.out.println("Received empty message.");
      return;
    }

    System.out.println(message);

    JsonObject o = parser.parse(message).getAsJsonObject();

    String command = o.get("command").getAsString();
    List<String> args = Lists.newArrayList();
    for(JsonElement e : o.get("args").getAsJsonArray()){
      args.add(e.getAsString());
    }

    handle(connection, command, args);
  }

  private void handle(WebSocketConnection from, String command, List<String> args) {
    if (command.equalsIgnoreCase("login")) {
      long userID = Long.parseLong(args.get(0));
      connectionIds.put(from, userID);
      
      String name = loginService.handleLogin(userID);

      if (name == null) {
        from.send(constructJson("PROMPT_NAME"));
        return;
      }

      connectionNameMap.put(from, name);
      nameConnectionMap.put(name, from);

      from.send(constructJson("LOGIN_SUCCESS", "username", name));

      sendRemote(createPlayersObject());

    } else if (command.equalsIgnoreCase("username")) {
      long userID = connectionIds.get(from);
      String name = args.get(0);
      loginService.createAccount(userID, name);
      from.send(constructJson("LOGIN_SUCCESS", "username", name));

      connectionNameMap.put(from, name);
      nameConnectionMap.put(name, from);

      sendRemote(createPlayersObject());
    } else if (command.equalsIgnoreCase("chat")) {
      String sender = connectionNameMap.get(from);
      if (sender == null) {
        from.send("You must login before chatting!");
        return;
      }

      handleChat(sender, args.get(0));
    }
  }

  private void handleChat(String sender, String message) {
    if (!message.startsWith("/")) {
      Player player = null;
      try {
        player = stage.getPlayer(sender);
      } catch (Exception e) {
      }

      if (moderated) {
        if (player == null || !player.isAlive()) {
          spectatorChat(sender, message);
          return;
        } else if (!playersAllowedToSpeak.contains(sender.toLowerCase())) {
          if (player.getRole().getFaction() != Faction.WOLVES) {
            sendMessage(sender, "You cannot speak at night.");
            return;
          }
        }
      }

      getStage().handleChat(this, sender, message);
      return;
    }

    List<String> m = ImmutableList.copyOf(Splitter.on(" ").split(message));

    String command = m.get(0).substring(1);
    List<String> args = m.subList(1, m.size());

    try {
      getStage().handle(this, sender, command, args);
    } catch (WolfException e) {
      sendMessage(sender, e.getMessage());
    }
  }

  private Set<WebSocketConnection> getSpectators() {
    Set<String> alivePlayers = Sets.newHashSet();
    for (Player p : stage.getAllPlayers()) {
      if (p.isAlive()) {
        alivePlayers.add(p.getName());
      }
    }
    Set<WebSocketConnection> ret = Sets.newHashSet();
    for (WebSocketConnection conn : ImmutableSet.copyOf(allConnections)) {
      String user = connectionNameMap.get(conn);
      if (!alivePlayers.contains(user)) {
        ret.add(conn);
      }
    }
    return ret;
  }

  public void spectatorChat(String from, String message) {
    String s = constructJson("S_CHAT", "from", from, "msg", message);
    for (WebSocketConnection conn : getSpectators()) {
      try {
        conn.send(s);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void sendToAll(String from, String message) {
    String s = constructChatJson(from, message);
    sendRemote(s);
  }

  private String constructChatJson(String from, String message) {
    return constructJson("CHAT", "from", from, "msg", message);
  }

  private String constructJson(String command, String... params) {
    JsonObject o = new JsonObject();

    o.addProperty("command", command);

    for (int i = 0; i < params.length; i += 2) {
      o.addProperty(params[i], params[i + 1]);
    }

    return o.toString();
  }

  @Override
  public void sendMessage(String message) {
    sendToAll("$narrator", message);
  }

  @Override
  public void sendMessage(String user, String message) {
    WebSocketConnection conn = nameConnectionMap.get(user);
    if (conn == null) {
      System.out.println("Tried to send message to offline user: " + user + " :: " + message);
      return;
    }
    String s = constructChatJson("$narrator", message);
    conn.send(s);
  }

  @Override
  public void setStage(Stage stage) {
    this.stage = stage;
  }

  @Override
  public Stage getStage() {
    return stage;
  }

  @Override
  public void onMessage(String sender, String message) {
    throw new RuntimeException("This should never be called.");
  }

  @Override
  public void muteAll() {
    moderated = true;
    playersAllowedToSpeak.clear();
  }

  @Override
  public void unmuteAll() {
    moderated = false;
    playersAllowedToSpeak.clear();
  }

  @Override
  public void unmute(String player) {
    playersAllowedToSpeak.add(player.toLowerCase());
  }

  @Override
  public void onPlayersChanged() {
    sendRemote(createPlayersObject());
  }

  private String createPlayersObject() {
    JsonObject o = new JsonObject();

    o.addProperty("command", "PLAYERS");
    o.addProperty("num_viewers", allConnections.size());

    JsonArray alive = new JsonArray();
    JsonArray dead = new JsonArray();
    JsonArray watchers = new JsonArray();

    Set<String> players = Sets.newHashSet();

    for (Player p : Sets.newTreeSet(stage.getAllPlayers())) {
      players.add(p.getName());
      JsonPrimitive e = new JsonPrimitive(p.getName());
      if (p.isAlive()) {
        alive.add(e);
      } else {
        dead.add(e);
      }
    }

    for (String s : ImmutableSet.copyOf(nameConnectionMap.keySet())) {
      if (!players.contains(s)) {
        watchers.add(new JsonPrimitive(s));
      }
    }

    o.add("alive", alive);
    o.add("dead", dead);
    o.add("watchers", watchers);

    return o.toString();
  }

  private void sendRemote(String s) {
    for (WebSocketConnection conn : ImmutableList.copyOf(allConnections)) {
      try {
        conn.send(s);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void recordGameResults(GameStage stage) {
    history.record(stage);
  }

}
