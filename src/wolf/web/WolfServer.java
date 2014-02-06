package wolf.web;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServers;

public class WolfServer implements HttpHandler {

  @Override
  public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control)
      throws Exception {
    String uri = request.uri();

    if (uri.equals("/")) {
      uri = "/wolf.html";
    }

    // System.out.println("uri: " + uri);

    byte[] data;

    try {
      URL url = WolfServer.class.getResource("rez" + uri);
      if (url == null) {
        response.status(404).end();
        return;
      }
      data = Resources.toByteArray(url);
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }

    response.content(data).end();
  }

  public static void main(String[] args) throws Exception {
    WebServers.createWebServer(80).add("/socket", new WebSocketHandler()).add(new WolfServer())
        .start().get();
    System.out.println("Server Started.");
  }

}
