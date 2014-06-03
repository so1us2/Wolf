package wolf.model.role;


public class Wolf extends AbstractWolfRole {

  @Override
  public String getDescription() {
    return "The Wolves kill a villager every night. They win when their numbers equal those of the villagers."
        + " They can communicate openly at night using wolf chat.";
  }

}
