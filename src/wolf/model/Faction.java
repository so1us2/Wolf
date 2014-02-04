package wolf.model;

public enum Faction {

  VILLAGERS("Villager", "Villagers"), WOLVES("Wolf", "Wolves"), DEMONS("Demon", "Demons");

  private final String singularForm, pluralForm;

  private Faction(String singular, String plural) {
    this.singularForm = singular;
    this.pluralForm = plural;
  }

  public String getSingularForm() {
    return singularForm;
  }

  public String getPluralForm() {
    return pluralForm;
  }

}
