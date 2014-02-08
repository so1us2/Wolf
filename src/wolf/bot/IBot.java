package wolf.bot;

import wolf.model.stage.Stage;

public interface IBot {

  public void sendMessage(String message);

  /**
   * Sends a private message to the given user.
   */
  public void sendMessage(String user, String message);

  public void sendToAll(String from, String message);

  public void setStage(Stage stage);

  public Stage getStage();

  public void onMessage(String sender, String message);

  public void muteAll();

  public void unmuteAll();

  public void unmute(String player);

  public void onPlayersChanged();

  // public void unmutePlayers(List<String> players);

}
