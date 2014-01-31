package wolf.model.role;

import wolf.model.Role;

public abstract class AbstractRole {

  private final Role role;

  public AbstractRole(Role role) {
    this.role = role;
  }

  public Role getType() {
    return role;
  }

  public void onDayBegins() {}

  public void onDayEnds() {}

  public void onNightBegins() {}

  public void onNightEnds() {}

}
