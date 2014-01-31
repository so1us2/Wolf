package wolf.engine.spell;

import wolf.engine.WolfEngine;

public abstract class Spell implements Comparable<Spell> {

  public abstract void execute(WolfEngine engine);

  /**
   * Higher priority gets executed faster.
   */
  public int getPriority() {
    return 0;
  }

  @Override
  public int compareTo(Spell o) {
    return o.getPriority() - getPriority();
  }

}
