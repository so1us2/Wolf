package wolf;

import com.google.common.base.Throwables;
import ez.DB;

public class WolfDB {

  private static DB db;

  private static void init() {
    try {
      db = new DB("localhost", "root", "", "wolf");
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
