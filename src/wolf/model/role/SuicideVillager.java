package wolf.model.role;


public class SuicideVillager extends AbstractRole {

  @Override
  public String getDescription() {
    return "An ordinary villager, except if lynched on the first day, wins the game (and everyone else loses).";
  }

}
