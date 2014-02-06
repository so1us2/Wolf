package wolf.web;

import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.webbitserver.BaseWebSocketHandler;
import org.webbitserver.WebSocketConnection;

public class WebSocketHandler extends BaseWebSocketHandler {

  private final Map<WebSocketConnection, String> connectionNameMap = Maps.newConcurrentMap();
  private final List<WebSocketConnection> allConnections = Lists.newArrayList();

  @Override
  public void onOpen(WebSocketConnection connection) {
    allConnections.add(connection);
    sendToAll("CONNECTIONS " + allConnections.size());
  }

  @Override
  public void onClose(WebSocketConnection connection) {
    allConnections.remove(connection);
    connectionNameMap.remove(connection);
    sendToAll("CONNECTIONS " + allConnections.size());
  }

  @Override
  public void onMessage(WebSocketConnection connection, String message) {
    message = message.trim();
    if (message.isEmpty()) {
      System.out.println("Received empty message.");
      return;
    }
    int i = message.indexOf(' ');
    if (i == -1) {
      System.out.println("Invalid message: " + message);
      return;
    }

    String command = message.substring(0, i);
    String s = message.substring(i + 1);

    if (command.equalsIgnoreCase("login")) {
      connectionNameMap.put(connection, s);
    } else if (command.equalsIgnoreCase("chat")) {
      String sender = connectionNameMap.get(connection);
      if (sender == null) {
        connection.send("You must login before chatting!");
        return;
      }
      s = "CHAT <" + sender + "> " + s;
      sendToAll(s);
    }
  }

  private void sendToAll(String s) {
    for (WebSocketConnection conn : ImmutableList.copyOf(allConnections)) {
      conn.send(s);
    }
  }

}
