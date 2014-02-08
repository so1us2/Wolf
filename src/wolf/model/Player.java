package wolf.model;

import wolf.model.role.AbstractRole;

import com.google.common.base.Objects;

public class Player implements Comparable<Player> {

  private final String name;
  private final boolean admin;

  private AbstractRole role;
  private boolean alive = true;

  private boolean winner = false;

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

  public void setAlive(boolean alive) {
    this.alive = alive;
  }

  public boolean isAlive() {
    return alive;
  }

  public void setRole(AbstractRole role) {
    this.role = role;
  }

  public AbstractRole getRole() {
    return role;
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

  @Override
  public String toString() {
    return name;
  }

  @Override
  public int compareTo(Player o) {
    return name.compareTo(o.name);
  }

  public void markWinner() {
    this.winner = true;
  }

  public boolean isWinner() {
    return winner;
  }

}
