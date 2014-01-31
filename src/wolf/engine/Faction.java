package wolf.engine;

public enum Faction {

  VILLAGERS, WOLVES, NEUTRAL;

  @Override
  public String toString() {

    if (this == VILLAGERS) {
      return "Villagers";
    } else if (this == WOLVES) {
      return "Wolves";
    } else if (this == NEUTRAL) {
      return "Neutral";
    } else
      return "None";
  };

}
