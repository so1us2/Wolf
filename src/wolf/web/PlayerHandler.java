package wolf.web;

import static com.google.common.base.Preconditions.checkState;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

public class PlayerHandler implements HttpHandler {

  private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("M/d/yyyy h:mm a");

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
            + "' AND a.game_id = b.id AND b.rated = true ORDER BY start_date DESC");

    JsonArray ret = new JsonArray();

    for (Row row : rows) {
      JsonObject o = new JsonObject();
      o.addProperty("role", row.get("role"));
      o.addProperty("winner", row.getBoolean("winner"));
      o.addProperty("alive", row.getBoolean("alive"));
      o.addProperty("rated", row.getBoolean("rated"));
      o.addProperty("num_players", row.getInt("num_players"));
      o.addProperty("start_date", format(row.getDateTime("start_date")));
      o.addProperty("end_date", format(row.getDateTime("end_date")));
      ret.add(o);
    }

    response.content(ret.toString()).end();
  }

  private String format(LocalDateTime time) {
    return time.format(format);
  }

}
