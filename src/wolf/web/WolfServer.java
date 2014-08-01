package wolf.web;

import java.io.IOException;
import java.net.URL;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServers;
import wolf.model.Role;
import wolf.rankings.RankingsHandler;
import com.google.common.base.Charsets;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class WolfServer implements HttpHandler {

  public static final boolean TEST_MODE = false;
  public static final int SEASON = 5; // also change rankings.js and wolf.js.starize() and modals.html

  private String modalHTML = null;

  private String getModalHTML() {
    if (modalHTML != null) {
      return modalHTML;
    }

    URL url = WolfServer.class.getResource("rez/modals.html");
    try {
      String ret = Resources.toString(url, Charsets.UTF_8);

      StringBuilder sb = new StringBuilder();
      for (Role role : Role.values()) {
        sb.append("<option value='").append(role.name()).append("'>").append(role.toString())
            .append("</option>");
      }
      ret = ret.replace("$ROLES", sb.toString());

      modalHTML = ret;

      return ret;
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control)
      throws Exception {
    String uri = request.uri();

    String host = request.header("Host");
    if (host != null && host.startsWith("www")) {
      System.out.println("Redirecting from WWW");
      response.header("Location", "http://playwolf.net").status(302).end();
      return;
    }

    if (uri.equals("/") || uri.startsWith("/room/")) {
      uri = "/wolf.html";
    }

    // System.out.println("uri: " + uri);

    byte[] data;

    try {
      URL url = WolfServer.class.getResource("rez" + uri);
      if (url == null) {
        System.out.println("404: " + uri);
        response.status(404).end();
        return;
      }
      if (uri.endsWith("wolf.js")) {
        String s = Resources.toString(url, Charsets.UTF_8);
        s = s.replace("$TESTING", TEST_MODE + "");
        data = s.getBytes(Charsets.UTF_8);
      } else {
        data = Resources.toByteArray(url);
      }
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }

    if (uri.equalsIgnoreCase("/wolf.html")) {
      String s = new String(data, Charsets.UTF_8);
      s = s.replace("$modals.html", getModalHTML());
      data = s.getBytes(Charsets.UTF_8);
    }

    response.content(data).end();
  }

  public static void main(String[] args) throws Exception {
    GameRouter bot = new GameRouter();
    WolfServer server = new WolfServer();
    
    WebServers.createWebServer(80).add("/socket", bot)
    .add(".*js", server)
    .add("/rankings.*", new RankingsHandler())
    .add("/player.*", new PlayerHandler())
    .add("/rules.*", new RulesHandler())
    .add("/rooms.*", new RoomHandler(bot))
    .add(server).start().get();
    System.out.println("Server Started.");
  }

}
