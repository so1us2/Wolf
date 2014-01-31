package wolf.arch;


public class Utils {

  public static String getDisplayName(Class<?> c, boolean plural) {
    DisplayName displayName = c.getAnnotation(DisplayName.class);

    if (displayName == null) {
      return c.getSimpleName();
    }

    return plural ? displayName.plural() : displayName.value();
  }

}
