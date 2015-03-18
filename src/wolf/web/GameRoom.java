package wolf.web;

import static jasonlib.util.Utils.isAlphaNumeric;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import wolf.ChatLogger;
import wolf.WolfDB;
import wolf.WolfException;
import wolf.bot.IBot;
import wolf.model.Faction;
import wolf.model.Player;
import wolf.model.stage.GameStage;
import wolf.model.stage.InitialStage;
import wolf.model.stage.Stage;
import wolf.rankings.GameHistory;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GameRoom implements IBot {

  public static final String MAIN_ROOM = "Main Room";
  public static final String NARRATOR = "$narrator";

  public String name;

  private final LoginService loginService = new LoginService();

  private boolean moderated = false;
  private Set<String> playersAllowedToSpeak = Sets.newHashSet();

  private final GameHistory history = new GameHistory();

  private List<ConnectionInfo> connections = Lists.newCopyOnWriteArrayList();

  private Stage stage = new InitialStage(this);

  private ChatLogger logger;

  public GameRoom() {
    this(MAIN_ROOM);
  }

  public GameRoom(String name) {
    this.name = name;
  }

  @Override
  public boolean isAdmin(String user) {
    return loginService.isAdmin(user);
  }

  public void onJoin(ConnectionInfo info) {
    System.out.println(info + " joined room: " + name);

    connections.add(info);
    info.setRoom(this);

    onPlayersChanged();

    if (stage instanceof GameStage) {
      GameStage gs = (GameStage) stage;
      info.getConnection().send(GameRouter.constructJson("TIMER", "end", gs.getEndTime()));
    }
    info.getConnection().send(GameRouter.constructJson("STAGE", "stage", stage.getStageIndex()));
  }

  /**
   * @return True if this room should be destroyed
   */
  public boolean onLeave(ConnectionInfo info) {
    System.out.println(info + " left room: " + name);
    if (!connections.remove(info)) {
      System.out.println("Failed to remove: " + info);
    }
    info.setRoom(null);

    onPlayersChanged();

    return !name.equals(MAIN_ROOM) && connections.isEmpty();
  }

  public void handleChat(String sender, String message) {
    if (message.startsWith("/")) {
      handleCommand(sender, message);
    } else {
      handleNormalChat(sender, message);
    }
  }

  private void handleCommand(String sender, String message) {
    List<String> m = ImmutableList.copyOf(Splitter.on(" ").split(message));

    String command = m.get(0).substring(1);
    List<String> args = m.subList(1, m.size());

    boolean isAdmin = isAdmin(sender);

    if (command.equals("enable-sounds")) {
      boolean enableSounds = Boolean.valueOf(args.get(0));
      loginService.setSoundsEnabled(sender, enableSounds);
    } else if (command.equals("play")) {
      if (isAdmin) {
        sendRemote(GameRouter.constructJson("MUSIC", "url", args.get(0)));
      }
    } else if (command.equalsIgnoreCase("pm")) {
      if (isAdmin) {
        String target = args.get(0);
        sendMessage(target, sender + ": " + Joiner.on(' ').join(args.subList(1, args.size())));
        sendMessage(sender, "PM Sent.");
      }
    } else if (command.equals("promote")) {
      if (isAdmin) {
        loginService.setAdmin(args.get(0), true);
        sendMessage(sender, args.get(0) + " is now an admin.");
      }
    } else if (command.equals("demote")) {
      if (isAdmin && !args.get(0).equalsIgnoreCase("satnam")) {
        loginService.setAdmin(args.get(0), false);
        sendMessage(sender, args.get(0) + " is demoted.");
      }
    } else if (command.equalsIgnoreCase("ban")) {
      System.out.println("ban: " + args);
      if (isAdmin) {
        GameRouter.banned.add(args.get(0).toLowerCase());
        sendMessage(sender, "banlist: " + GameRouter.banned);
      }
    } else if (command.equalsIgnoreCase("unban")) {
      System.out.println("unban: " + args);
      if (isAdmin) {
        GameRouter.banned.remove(args.get(0).toLowerCase());
        sendMessage(sender, "banlist: " + GameRouter.banned);
      }
    } else if (command.equalsIgnoreCase("nick")) {
      String oldNick = sender;
      String newNick = args.get(0);

      if (!isAlphaNumeric(newNick)) {
        throw new RuntimeException("Invalid name: " + name);
      }

      if (isAdmin) {
        System.out.println("Changing nick: " + oldNick + " to " + newNick);
        WolfDB.get().execute("UPDATE users SET name = '" + newNick + "' WHERE name = '" + oldNick + "'");
        WolfDB.get().execute("UPDATE players SET name = '" + newNick + "' WHERE name = '" + oldNick + "'");
        sendMessage(sender, "Your name has been switched. Refresh the page.");
      }
    } else {
      try {
        getStage().handle(this, sender, command, args);
      } catch (WolfException e) {
        sendMessage(sender, e.getMessage());
      }
    }
  }

  private void handleNormalChat(String sender, String message) {
    Player player = null;
    try {
      player = stage.getPlayer(sender);
    } catch (Exception e) {}

    if (moderated) {
      if (player == null || !player.isAlive()) {
        spectatorChat(sender, message);
        return;
      } else if (!playersAllowedToSpeak.contains(sender.toLowerCase())) {
        if (player.getRole().getFaction() != Faction.WOLVES) {
          sendMessage(sender, "You cannot speak at night.");
          return;
        }
      }
    }

    getStage().handleChat(this, sender, message);
  }

  private Set<ConnectionInfo> getSpectators() {
    Set<String> alivePlayers = Sets.newHashSet();
    for (Player p : stage.getAllPlayers()) {
      if (p.isAlive()) {
        alivePlayers.add(p.getName());
      }
    }
    Set<ConnectionInfo> ret = Sets.newHashSet();
    for (ConnectionInfo conn : connections) {
      String user = conn.getName();
      if (!alivePlayers.contains(user)) {
        ret.add(conn);
      }
    }
    return ret;
  }

  public void spectatorChat(String from, String message) {
    String s = GameRouter.constructJson("S_CHAT", "from", from, "msg", message);
    for (ConnectionInfo conn : getSpectators()) {
      try {
        conn.send(s);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void sendMessage(String message) {
    if (logger != null) {
      logger.narrator(message);
    }

    sendToAll(NARRATOR, message);
  }

  @Override
  public void sendMessage(String user, String message) {
    if (logger != null) {
      logger.pm(user, message);
    }

    // todo fix this so that you can't have multiple conns per person
    List<ConnectionInfo> conns = Lists.newArrayList();
    for (ConnectionInfo conn : connections) {
      if (user.equalsIgnoreCase(conn.getName())) {
        conns.add(conn);
      }
    }
    if (conns.size() == 0) {
      System.out.println("Tried to send message to offline user: " + user + " :: " + message);
      return;
    }
    String s = GameRouter.constructChatJson(NARRATOR, message);
    for (ConnectionInfo conn : conns) {
      conn.send(s);
    }
  }

  @Override
  public void setStage(Stage stage) {
    System.out.println("Setting stage to: " + stage + " in room: " + name);
    this.stage = stage;

    sendToAll("STAGE", "stage", stage.getStageIndex());
  }

  @Override
  public Stage getStage() {
    return stage;
  }

  @Override
  public void onMessage(String sender, String message) {
    throw new RuntimeException("This should never be called.");
  }

  @Override
  public void muteAll() {
    moderated = true;
    playersAllowedToSpeak.clear();
  }

  @Override
  public void unmuteAll() {
    moderated = false;
    playersAllowedToSpeak.clear();
  }

  @Override
  public void unmute(String player) {
    playersAllowedToSpeak.add(player.toLowerCase());
  }

  @Override
  public void onPlayersChanged() {
    if (stage != null) {
      sendRemote(createPlayersObject());
    }
  }

  private String createPlayersObject() {
    JsonObject o = new JsonObject();

    o.addProperty("command", "PLAYERS");

    JsonArray players = new JsonArray();

    TreeSet<String> set = Sets.newTreeSet(String.CASE_INSENSITIVE_ORDER);

    int notSignedIn = 0;
    for (ConnectionInfo info : connections) {
      if (info.getName() == null) {
        notSignedIn++;
      } else {
        set.add(info.getName());
      }
    }

    for (Player p : stage.getAllPlayers()) {
      set.add(p.getName());
    }

    for (String s : set) {
      JsonObject p = new JsonObject();

      p.addProperty("name", s);

      if (isAdmin(s)) {
        p.addProperty("admin", true);
      }

      Player player = stage.getPlayerOrNull(s);
      if (player != null) {
        p.addProperty("in_game", true);
        if (player.isAlive()) {
          p.addProperty("alive", true);
        }

        ConnectionInfo conn = null;
        for (ConnectionInfo info : connections) {
          if (s.equals(info.getName())) {
            conn = info;
            break;
          }
        }
        if (conn == null) {
          p.addProperty("disconnected", true);
        }

        if (stage instanceof GameStage) {
          Map<Player, Player> votes = ((GameStage) stage).getVotesToDayKill();
          if (votes.containsKey(player)) {
            p.addProperty("voted", true);
          }
        }
      }

      players.add(p);
    }

    o.addProperty("num_not_signed_in", notSignedIn);
    o.add("players", players);

    return o.toString();
  }

  @Override
  public void sendToAll(String command, Object... params) {
    sendRemote(GameRouter.constructJson(command, params));
  }

  private void sendRemote(String s) {
    for (ConnectionInfo conn : connections) {
      conn.send(s);
    }
  }

  @Override
  public void recordGameResults(GameStage stage) {
    history.record(stage);
  }

  @Override
  public void sendToAll(String from, String message) {
    String s = GameRouter.constructChatJson(from, message);
    sendRemote(s);
  }

  @Override
  public void setLogger(ChatLogger logger) {
    this.logger = logger;
  }

}
