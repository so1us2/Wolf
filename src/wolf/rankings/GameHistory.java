package wolf.rankings;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import wolf.WolfDB;
import wolf.model.Player;
import wolf.model.stage.GameStage;
import wolf.web.WolfServer;
import com.google.common.collect.Lists;
import ez.DB;
import ez.Row;
import ez.Table;

public class GameHistory {

  private DB db = WolfDB.get();

  public GameHistory() {
    if (db != null) {
      if (!db.hasTable("games")) {
        db.addTable(new Table("games")
            .primary("id", UUID.class)
            .column("rated", Boolean.class)
            .column("start_date", LocalDateTime.class)
            .column("end_date", LocalDateTime.class)
            .column("num_players", Integer.class)
            .column("season", Integer.class)
            );
      }
      if (!db.hasTable("players")) {
        db.addTable(new Table("players")
            .column("game_id", UUID.class)
            .column("name", String.class)
            .column("role", String.class)
            .column("winner", Boolean.class)
            .column("alive", Boolean.class));
      }
    }
  }

  public void record(GameStage stage) {
    System.out.println("Recording game...");

    LocalDateTime start = stage.getStartDate();
    LocalDateTime end = LocalDateTime.now();
    boolean rated = stage.getConfig().isRated();

    List<Row> players = Lists.newArrayList();
    for (Player p : stage.getAllPlayers()) {
      players.add(new Row()
          .with("game_id", stage.getId())
          .with("name", p.getName())
          .with("role", p.getRole().getType().toString())
          .with("winner", p.isWinner())
          .with("alive", p.isAlive()));
    }

    db.insert("games",
        new Row()
            .with("id", stage.getId())
            .with("rated", rated)
            .with("start_date", start)
            .with("end_date", end)
            .with("num_players", players.size())
            .with("season", WolfServer.SEASON));

    db.insert("players", players);

    System.out.println("Game Recorded.");
  }

}
