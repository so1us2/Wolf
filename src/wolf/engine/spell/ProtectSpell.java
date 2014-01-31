package wolf.engine.spell;

import wolf.engine.Player;
import wolf.engine.WolfEngine;

public class ProtectSpell extends Spell {

  private final Player target;

  public ProtectSpell(Player target) {
    this.target = target;
  }

  @Override
  public void execute(WolfEngine engine) {
    target.getRole().setProtected(true);
  }

  @Override
  public int getPriority() {
    return 10;
  }

}
