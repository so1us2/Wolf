package wolf.web;

import java.net.URL;
import java.util.List;

import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;

public class RulesHandler implements HttpHandler {

  @Override
  public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control)
      throws Exception {

    String uri = request.uri();
    System.out.println(uri);

    List<String> m =
        ImmutableList.copyOf(Splitter.on("/").trimResults().omitEmptyStrings().split(uri));

    if (m.size() == 1) {
      JsonArray ret = new JsonArray();

      ret.add(new JsonPrimitive("Objectives"));
      ret.add(new JsonPrimitive("Villager"));
      ret.add(new JsonPrimitive("Wolf"));
      ret.add(new JsonPrimitive("Seer"));
      ret.add(new JsonPrimitive("Priest"));
      // for (Role role : Role.values()) {
      // ret.add(new JsonPrimitive(role.toString()));
      // }

      response.content(ret.toString()).end();
    } else {
      String file = m.get(1).toLowerCase() + ".html";
      URL url = RulesHandler.class.getResource("rez/rules/" + file);
      if (url == null) {
        response.content("No rules written for this yet.").end();
      } else {
        byte[] data = Resources.toByteArray(url);
        response.content(data).end();
      }
    }
  }

}
