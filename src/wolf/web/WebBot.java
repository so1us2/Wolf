package wolf.web;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;
import wolf.WolfException;
import wolf.bot.IBot;
import wolf.model.stage.InitialStage;
import wolf.model.stage.Stage;

public class WebBot extends BaseWebSocketHandler implements IBot {

  private final Map<WebSocketConnection, String> connectionNameMap = Maps.newConcurrentMap();
  private final Map<String, WebSocketConnection> nameConnectionMap = Maps.newConcurrentMap();
  private final List<WebSocketConnection> allConnections = Lists.newArrayList();

  private final JsonParser parser = new JsonParser();

  private Stage stage = new InitialStage(this);

  private boolean moderated = false;
  private Set<String> playersAllowedToSpeak = Sets.newHashSet();

  @Override
  public void onOpen(WebSocketConnection connection) {
    allConnections.add(connection);
    sendToAll("CONNECTIONS", allConnections.size());
  }

  @Override
  public void onClose(WebSocketConnection connection) {
    allConnections.remove(connection);
    String user = connectionNameMap.remove(connection);
    if (user != null) {
      nameConnectionMap.remove(user);
    }
    sendToAll("CONNECTIONS", allConnections.size());
  }

  @Override
  public void onMessage(WebSocketConnection connection, String message) {
    message = message.trim();
    if (message.isEmpty()) {
      System.out.println("Received empty message.");
      return;
    }

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
      connectionNameMap.put(from, args.get(0));
      nameConnectionMap.put(args.get(0), from);
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
      if (moderated && !playersAllowedToSpeak.contains(sender.toLowerCase())) {
        sendMessage(sender, "You do not have permission to speak right now.");
        return;
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

  @Override
  public void sendToAll(String from, String message) {
    sendToAll("CHAT", from, message);
  }

  private void sendToAll(String command, Object... args) {
    String s = constructJson(command, args);

    for (WebSocketConnection conn : ImmutableList.copyOf(allConnections)) {
      conn.send(s);
    }
  }

  private String constructJson(String command, Object... args) {
    JsonObject o = new JsonObject();
    o.addProperty("command", command);

    JsonArray argsArray = new JsonArray();
    for (Object arg : args) {
      argsArray.add(new JsonPrimitive(arg.toString()));
    }
    o.add("args", argsArray);

    return o.toString();
  }

  @Override
  public void sendMessage(String message) {
    sendToAll("CHAT", "$narrator", message);
  }

  @Override
  public void sendMessage(String user, String message) {
    WebSocketConnection conn = nameConnectionMap.get(user);
    if (conn == null) {
      System.out.println("Tried to send message to offline user: " + user + " :: " + message);
      return;
    }
    String s = constructJson("CHAT", "$narrator", message);
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

}
