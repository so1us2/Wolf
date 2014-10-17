package wolf.rankings;

import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import wolf.WolfDB;
import wolf.model.Faction;
import wolf.model.Role;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import ez.DB;
import ez.Row;

public class StatsAnalyzer {

  private static final int NUM_FACTIONS = Faction.values().length;

  private DB db = WolfDB.get();

  public void run() {
    Map<Integer, Multimap<String, Row>> m = computeDataStructure();

    for (int i = 2; i <= NUM_FACTIONS; i++) {
      System.out.println(i + "-Faction Games");
      Multimap<String, Row> mm = m.get(i);
      for (String role : mm.keySet()) {
        int wins = 0, losses = 0;
        for (Row row : mm.get(role)) {
          boolean winner = row.get("winner");
          if (winner) {
            wins++;
          } else {
            losses++;
          }
        }
        // double p = 100d * wins / (wins + losses);
        System.out.println(role + "\t" + wins + "\t" + losses);
      }
    }
  }

  private Map<Integer, Multimap<String, Row>> computeDataStructure() {
    Multimap<String, Row> m = ArrayListMultimap.create();
    for (Row row : db.select("SELECT * FROM players")) {
      m.put(row.<String>get("game_id"), row);
    }

    Map<Integer, Multimap<String, Row>> ret = Maps.newHashMap();
    for (int i = 2; i <= NUM_FACTIONS; i++) {
      ret.put(i, ArrayListMultimap.<String, Row>create());
    }

    outerloop: for (String game : m.keySet()) {
      if (m.get(game).size() != 9) {
        System.out.println("Skipping game.");
        continue;
      }
      Set<Faction> factions = EnumSet.noneOf(Faction.class);
      for (Row row : m.get(game)) {
        if (row.get("role").equals("Priest")) {
          continue outerloop;
        }
        factions.add(Role.parse(row.<String>get("role")).getFaction());
      }
      Multimap<String, Row> mm = ret.get(factions.size());
      for(Row row : m.get(game)){
        mm.put(row.<String>get("role"), row);
      }
    }

    return ret;
  }

  public static void main(String[] args) {
    new StatsAnalyzer().run();
  }

}
