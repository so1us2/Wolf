package wolf.web;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.webbitserver.HttpControl;
import org.webbitserver.HttpHandler;
import org.webbitserver.HttpRequest;
import org.webbitserver.HttpResponse;

import wolf.WolfDB;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ez.DB;
import ez.Row;
import static com.google.common.base.Preconditions.checkState;

public class PlayerHandler implements HttpHandler {

  private static final DateTimeFormatter format = DateTimeFormat.forPattern("M/d/yyyy h:mm aa");

  private final DB db = WolfDB.get();

  @Override
  public void handleHttpRequest(HttpRequest request, HttpResponse response, HttpControl control)
      throws Exception {
    if (db == null) {
      response.content("[]").end();
      return;
    }


    List<String> m = ImmutableList.copyOf(Splitter.on("/").omitEmptyStrings().split(request.uri()));

    String player = m.get(1);
    checkState("history".equals(m.get(2)));

    System.out.println("Getting rankings for: " + player);

    List<Row> rows =
        db.select("SELECT a.role, a.winner, a.alive, b.rated, b.start_date, b.end_date, b.num_players "
        + "FROM wolf.players a, wolf.games b " + "WHERE name = '" + player
        + "' AND a.game_id = b.id ORDER BY start_date DESC");

    JsonArray ret = new JsonArray();

    for (Row row : rows) {
      JsonObject o = new JsonObject();
      o.addProperty("role", row.<String>get("role"));
      o.addProperty("winner", row.<Boolean>get("winner"));
      o.addProperty("alive", row.<Boolean>get("alive"));
      o.addProperty("rated", row.<Boolean>get("rated"));
      o.addProperty("num_players", row.<Integer>get("num_players"));
      o.addProperty("start_date", format(row.<Long>get("start_date")));
      o.addProperty("end_date", format(row.<Long>get("end_date")));
      ret.add(o);
    }

    response.content(ret.toString()).end();
  }

  private String format(long millis) {
    DateTime dt = new DateTime(millis);
    return dt.toString(format);
  }

}
