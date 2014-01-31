package wolf.model;

import com.google.common.base.Objects;

public class Player {

  private final String name;
  private final boolean admin;

  public Player(String name, boolean admin) {
    this.name = name;
    this.admin = admin;
  }

  public String getName() {
    return name;
  }

  public boolean isAdmin() {
    return admin;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Player)) {
      return false;
    }
    Player p = (Player) obj;
    return Objects.equal(name, p.name);
  }

  @Override
  public int hashCode() {
    return name.hashCode();
  }

}
