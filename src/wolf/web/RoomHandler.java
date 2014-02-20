package wolf.web;

import java.util.List;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class RoomHandler implements HttpHandler {

  private final GameRouter router;

  public RoomHandler(GameRouter router) {
    this.router = router;
  }

  @Override
  public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control)
      throws Exception {
    if ("GET".equals(request.method())) {
      String roomInfo = createRoomInfo(router.getRooms()).toString();
      response.content(roomInfo).end();
    } else if ("POST".equals(request.method())) {
      String uri = request.uri();
      List<String> m = ImmutableList.copyOf(Splitter.on("/").omitEmptyStrings().split(uri));
      String roomName = m.get(1);

      roomName = roomName.replace(" ", "_");

      if (roomName.isEmpty() || roomName.contains("<")) {
        throw new RuntimeException("Bad room name: " + roomName);
      }
      
      router.createRoom(roomName);

      response.end();
    }
  }

  private static JsonArray createRoomInfo(List<GameRoom> rooms) {
    JsonArray ret = new JsonArray();
    for (GameRoom room : rooms) {
      ret.add(new JsonPrimitive(room.name));
    }
    return ret;
  }

}
