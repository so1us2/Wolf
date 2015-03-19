package wolf.web;

import static com.google.common.base.Preconditions.checkState;
import jasonlib.Json;
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

    Json ret = Json.array();

    for (Row row : rows) {
      Json o = Json.object();
      o.with("role", row.get("role"));
      o.with("winner", row.getBoolean("winner"));
      o.with("alive", row.getBoolean("alive"));
      o.with("rated", row.getBoolean("rated"));
      o.with("num_players", row.getInt("num_players"));
      o.with("start_date", format(row.getDateTime("start_date")));
      o.with("end_date", format(row.getDateTime("end_date")));
      ret.add(o);
    }

    response.content(ret.toString()).end();
  }

  private String format(LocalDateTime time) {
    return time.format(format);
  }

}
