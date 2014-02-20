package wolf.web;

import org.webbitserver.WebSocketConnection;

public class ConnectionInfo {

  private final WebSocketConnection connection;

  private Long userID;

  private String name;

  private GameRoom room;

  public ConnectionInfo(WebSocketConnection connection) {
    this.connection = connection;
  }

  public void setUserID(long userID) {
    this.userID = userID;
  }

  public Long getUserID() {
    return userID;
  }

  public void send(String s) {
    try {
      connection.send(s);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public GameRoom getRoom() {
    return room;
  }

  public void setRoom(GameRoom room) {
    this.room = room;
  }

  public WebSocketConnection getConnection() {
    return connection;
  }

  @Override
  public String toString() {
    return name;
  }

}
