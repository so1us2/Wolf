package wolf.web;

import java.io.IOException;
import java.net.URL;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;
import org.webbitserver.WebServers;

import wolf.rankings.RankingsHandler;

import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class WolfServer implements HttpHandler {

  @Override
  public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control)
      throws Exception {
    String uri = request.uri();

    if (request.header("Host").startsWith("www")) {
      System.out.println("Redirecting from WWW");
      response.header("Location", "http://playwolf.net").status(302).end();
      return;
    }

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
    WebServers.createWebServer(80).add("/socket", new WebBot())
    .add("/rankings", new RankingsHandler())
    .add("/rules.*", new RulesHandler())
    .add(new WolfServer()).start().get();
    System.out.println("Server Started.");
  }

}
