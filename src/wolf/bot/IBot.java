package wolf.bot;

import java.util.List;

import wolf.model.stage.Stage;

public interface IBot {

  public void sendMessage(String message);

  public void sendMessage(String user, String message);

  public void setStage(Stage stage);

  public Stage getStage();

  public void onMessage(String sender, String message, boolean isPrivate);

  public void muteAll();

  public void unmuteAll();

  public void unmute(String player);

  public void unmutePlayers(List<String> players);

}
