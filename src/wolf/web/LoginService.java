package wolf.web;

import java.util.List;

import com.google.common.collect.Iterables;
import ez.DB;
import ez.Row;
import ez.Table;
import wolf.WolfDB;

public class LoginService {

  private DB db = WolfDB.get();

  public LoginService() {
    if (db != null && !db.hasTable("users")) {
      db.addTable(new Table("users").primary("id", Long.class).column("name", String.class)
          .column("enable_sounds", Boolean.class));
    }
  }

  public User handleLogin(long userID) {
    // if (userID == 1) {
    // return new User("Test Account", true);
    // }

    List<Row> rows = db.select("SELECT name, enable_sounds FROM users WHERE id = " + userID);
    if (rows.isEmpty()) {
      db.insert("users", new Row().with("id", userID));
      return null;
    }

    Row row = Iterables.getOnlyElement(rows);

    return new User(row.<String>get("name"), row.<Boolean>get("enable_sounds"));
  }

  public void createAccount(long userID, String name) {
    db.update("UPDATE users SET name = ? WHERE id = ?", name, userID);
  }

  public void setSoundsEnabled(String user, boolean enableSounds) {
    db.update("UPDATE users SET enable_sounds = ? WHERE name = ?", enableSounds, user);
  }

  public static class User {
    public final String name;
    public final boolean enableSounds;

    public User(String name, boolean enableSounds) {
      this.name = name;
      this.enableSounds = enableSounds;
    }
  }

}
