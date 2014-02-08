package wolf;

import wolf.rankings.Config;
import ez.DB;

public class WolfDB {

  private static DB db;

  private static void init() {
    Config config = Config.load("PlayWolf");
    db = new DB(config.get("db_ip"), config.get("db_user"), config.get("db_password"), "wolf");
    System.out.println("Connected to DB.");
  }

  public static DB get() {
    if (db == null) {
      init();
    }
    return db;
  }

}
