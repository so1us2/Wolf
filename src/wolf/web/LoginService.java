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
      db.addTable(new Table("users").primary("id", Long.class).column("name", String.class));
    }
  }

  public String handleLogin(long userID) {
    if (userID == 1) {
      return "Test Account";
    }

    List<Row> rows = db.select("SELECT name FROM users WHERE id = " + userID);
    if (rows.isEmpty()) {
      db.insert("users", new Row().with("id", userID));
      return null;
    }
    return Iterables.getOnlyElement(rows).get("name");
  }

  public void createAccount(long userID, String name) {
    db.update("UPDATE users SET name = ? WHERE id = ?", name, userID);
  }

}
