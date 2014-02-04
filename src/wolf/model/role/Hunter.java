package wolf.model.role;

public class Hunter extends AbstractRole {

  @Override
  public String getDescription() {
    return "The Hunter has no special powers but if alive when the wolves would win the game, the village wins instead.";
  }

}
