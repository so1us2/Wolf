package wolf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;
import java.util.UUID;

import wolf.model.GameConfig;
import wolf.model.Player;

import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

public class ChatLogger {

  private final PrintStream out;

  public ChatLogger(UUID gameID, GameConfig config, Iterable<Player> players) {
    File f = new File("logs");
    if (!f.exists()) {
      f.mkdir();
    }
    f = new File(f, gameID.toString());
    try {
      out = new PrintStream(f);
    } catch (FileNotFoundException e) {
      throw Throwables.propagate(e);
    }
    out.println("Log started on " + System.currentTimeMillis());
    out.println("Rated: " + config.isRated());
    out.println("# Players: " + Iterables.size(players));
    for (Player player : players) {
      out.println(player.getName() + ": " + player.getRole().getType().name());
    }
  }

  public void chat(String sender, String message) {
    out.println(sender + ": " + message);
  }

  public void command(String sender, String command, List<String> args) {
    out.println(sender + ": /" + command + " " + Joiner.on(" ").join(args));
  }

  public void narrator(String message) {
    out.println("NARRATOR: " + message);
  }

  public void pm(String user, String message) {
    out.println("NARRATOR: /pm " + user + ": " + message);
  }

  public void close() {
    try {
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

}
