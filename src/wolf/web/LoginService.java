package wolf.web;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import wolf.WolfDB;

import com.beust.jcommander.internal.Sets;
import com.google.common.collect.Iterables;

import ez.DB;
import ez.Row;
import ez.Table;
import static com.google.common.base.Preconditions.checkState;

public class LoginService {

  private DB db = WolfDB.get();

  private final Set<String> admins = Sets.newHashSet();

  public LoginService() {
    if (db == null) {
      return;
    }
    if (!db.hasTable("users")) {
      db.addTable(new Table("users").primary("id", Long.class).column("name", String.class).column("enable_sounds", Boolean.class)
          .column("real_name", String.class).column("whitelist", Boolean.class).column("banned_until", DateTime.class)
          .column("admin", Boolean.class));
    }

    List<Row> rows = db.select("SELECT name FROM users WHERE admin = true");
    for (Row row : rows) {
      admins.add(row.<String>get("name").toLowerCase());
    }
  }

  public boolean isAdmin(String user) {
    return admins.contains(user.toLowerCase());
  }

  public void setAdmin(String user, boolean admin) {
    if (!StringUtils.isAlphanumeric(user)) {
      throw new RuntimeException("Invalid name: " + user);
    }
    db.execute("UPDATE users SET admin = " + admin + " WHERE name = '" + user+"'");
    if (admin) {
      admins.add(user.toLowerCase());
    } else {
      admins.remove(user.toLowerCase());
    }
  }

  public User handleLogin(long userID) {
    List<Row> rows = db.select("SELECT name, enable_sounds FROM users WHERE id = " + userID);
    if (rows.isEmpty()) {
      db.insert("users", new Row().with("id", userID));
      return null;
    }

    Row row = Iterables.getOnlyElement(rows);

    return new User(row.<String>get("name"), row.<Boolean>get("enable_sounds"));
  }

  public void createAccount(long userID, String name) {
    if (!StringUtils.isAlphanumeric(name)) {
      throw new RuntimeException("Invalid name: " + name);
    }

    int n = db.select("SELECT * FROM users WHERE id = " + userID+" AND name IS NOT NULL").size();
    checkState(n == 0, "This facebook account already has a userID.");

    n = db.select("SELECT * FROM users WHERE name = '" + name + "'").size();
    checkState(n == 0, "Duplicate username: " + name);

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
