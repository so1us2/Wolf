package wolf;

import wolf.rankings.Config;

import com.google.common.base.Throwables;

import ez.DB;

public class WolfDB {

  private static DB db;

  private static void init() {
    Config config = Config.load("PlayWolf");
    if (config.get("db_ip") == null) {
      System.out.println("DB not configured for this machine.");
      return;
    }
    try {
      db = new DB(config.get("db_ip"), config.get("db_user"), config.get("db_password"), "wolf");
      System.out.println("Connected to DB.");
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  public static DB get() {
    if (db == null) {
      init();
    }
    return db;
  }

}
