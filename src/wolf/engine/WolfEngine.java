package wolf.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.base.Predicate;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import wolf.GameHandler;
import wolf.GameInitializer;
import wolf.WolfBot;
import wolf.WolfException;
import wolf.action.game.AbstractGameAction;
import wolf.action.game.ListPlayersAction;
import wolf.action.game.ReplaceAction;
import wolf.action.game.VoteCountAction;
import wolf.engine.spell.KillSpell;
import wolf.engine.spell.Spell;
import wolf.role.GameRole;
import wolf.role.advanced.Demon;
import wolf.role.advanced.Twin;
import wolf.role.classic.Civilian;
import wolf.role.classic.Seer;
import wolf.role.classic.Wolf;

public class WolfEngine implements GameHandler {

  private final List<AbstractGameAction> actions = Lists.newArrayList(new ReplaceAction(),
      new ListPlayersAction(), new VoteCountAction());

  private final WolfBot bot;

  private final Map<String, Player> namePlayerMap;
  private final Map<String, WolfProperty> properties;

  private VotingHistory votingHistory;

  private Time time;

  int dayNumber = 0;

  private Faction winner = null;

  private final List<Spell> spells = Lists.newArrayList();

  public WolfEngine(WolfBot bot, GameInitializer initializer) {
    this.bot = bot;
    this.namePlayerMap = initializer.getNamePlayerMap();
    this.properties = initializer.getProperties();

    for (AbstractGameAction action : actions) {
      action.setEngine(this);
    }

    Time startingTime = getProperty(WolfProperty.STARTING_TIME);

    try {
      assignRoles(initializer.getRoleCountMap());
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }

    gameStartActions();

    begin(startingTime);
  }

  private void gameStartActions() {
    // Tell the wolves who all the wolves are
    List<Player> wolves = Lists.newArrayList(getAlivePlayers(Wolf.class));
    for (Player wolf : wolves) {
      bot.sendMessage(wolf, "The wolves are: " + wolves);
    }

    List<Player> twins = Lists.newArrayList(getAlivePlayers(Twin.class));
    for (Player twin : twins) {
      bot.sendMessage(twin, "The twins are: " + twins);
    }

    if (Iterables.size(getAlivePlayers()) >= 7) {
      List<Player> civs = Lists.newArrayList(getAlivePlayers(Civilian.class));
      if (!civs.isEmpty()) {
        Player randomPeek = civs.get((int) (Math.random() * civs.size()));
        for (Player seer : getAlivePlayers(Seer.class)) {
          bot.sendMessage(seer, randomPeek.getName() + " is a civilian.");
          ((Seer) seer.getRole()).addPeek(randomPeek);
        }
      }
    }

    if (!Iterables.isEmpty(getAlivePlayers(Demon.class))) {
      List<Player> civs = Lists.newArrayList(getAlivePlayers(Faction.VILLAGERS));
      if (!civs.isEmpty()) {
        Player randomPlayer = civs.get((int) (Math.random() * civs.size()));
        for (Player demon : getAlivePlayers(Demon.class)) {
          bot.sendMessage(demon, randomPlayer.getName() + " is your phylactery.");
          ((Demon) demon.getRole()).setPhylactery(randomPlayer);
        }
      }
    }
  }

  private void begin(Time time) {
    if (time == null) {
      throw new IllegalArgumentException("time can't be null.");
    }

    this.time = time;

    if (time == Time.Day) {

      dayNumber++;
      votingHistory = new VotingHistory();
      bot.sendMessage("Day " + dayNumber + " dawns on the village.");
      for (Player player : getAlivePlayers()) {
        bot.voice(WolfBot.channel, player.getName());
      }
    } else {
      bot.sendMessage("The world grows dark as the villagers drift to sleep.");
      bot.deVoiceAll();
    }

    for (Player player : getAlivePlayers()) {
      player.getRole().begin(time);
    }

    // check for the end right away, it's possible that nobody has any action to take.
    checkEndOfTimePeriod();
  }

  /**
   * Ends the current day/night
   */
  private void end() {
    Multimap<Class<? extends GameRole>, GameRole> roleMembers = ArrayListMultimap.create();

    for (Player player : getAlivePlayers()) {
      roleMembers.put(player.getRole().getClass(), player.getRole());
    }

    if (getTime() == Time.Day) {
      Player majorityVote = getMajorityVote();
      if (majorityVote == null) {
        bot.sendMessage("No majority was reached.");
        for (Player player : getAlivePlayers()) {
          player.getRole().setVoteTarget(null);
        }
        votingHistory.nextRound();
        return;
      } else {
        votingHistory.print(bot);
        bot.sendMessage("A verdict was reached and " + majorityVote + " was lynched.");
        // if (majorityVote.getRole().getFaction() == Faction.WOLVES) {
        // bot.sendMessage(majorityVote + " was a WOLF.");
        // } else {
        // bot.sendMessage(majorityVote + " was a villager.");
        // }

        bot.sendMessage(majorityVote + " was a " + majorityVote.getRole().getDayKillNotice());

        votingHistory = null;
        cast(new KillSpell(majorityVote));
      }
    }

    for (Class<? extends GameRole> roleClass : roleMembers.keySet()) {
      Collection<GameRole> members = roleMembers.get(roleClass);
      members.iterator().next().end(time, members);
    }

    executeSpells();

    checkForWinner();

    if (!isGameOver()) {
      if (time == Time.Day) {
        begin(Time.Night);
      } else if (time == Time.Night) {
        begin(Time.Day);
      } else {
        throw new IllegalStateException("Don't know how to end: " + time);
      }
    }
  }

  private Player getMajorityVote() {
    Map<Player, Integer> playerVotes = Maps.newHashMap();

    for (Player player : getAlivePlayers()) {
      Player target = player.getRole().getVoteTarget();
      Integer i = playerVotes.get(target);
      if (i == null) {
        i = 0;
      }
      playerVotes.put(target, i + 1);
    }

    int votesNeededToWin = (int) Math.ceil(Iterables.size(getAlivePlayers()) / 2.0);

    for (Entry<Player, Integer> e : playerVotes.entrySet()) {
      if (e.getValue() >= votesNeededToWin) {
        return e.getKey();
      }
    }

    return null;
  }

  public Map<Faction, Integer> getFactionCounts() {
    Map<Faction, Integer> ret = Maps.newHashMap();

    for (Faction f : Faction.values()) {
      ret.put(f, 0);
    }

    for (Player p : getAlivePlayers()) {
      Integer c = ret.get(p.getRole().getFaction());
      ret.put(p.getRole().getFaction(), ++c);
    }

    return ret;
  }

  public int getNumVotes() {
    int count = 0;

    for (Player player : getAlivePlayers()) {
      if (player.getRole().getVoteTarget() != null) {
        count++;
      }
    }
    return count;
  }

  public void roleChat(Class<? extends GameRole> targetClass, Player sender, String prefix,
      String message) {
    for (Player player : getAlivePlayers()) {
      if (!player.getName().equals(sender)
          && targetClass.isAssignableFrom(player.getRole().getClass())) {
        bot.sendMessage(player, prefix + sender + ": " + message);
      }
    }
  }

  public Map<String, Player> getNamePlayerMap() {
    return namePlayerMap;
  }

  private void checkForWinner() {
    Map<Faction, Integer> factionCount = getFactionCounts();

    int numAlive = Iterables.size(getAlivePlayers());

    if (factionCount.get(Faction.VILLAGERS) == numAlive) {
      setWinner(Faction.VILLAGERS);
      return;
    }

    if (factionCount.get(Faction.NEUTRAL) == 0) {
      if (factionCount.get(Faction.WOLVES) >= factionCount.get(Faction.VILLAGERS)) {
        setWinner(Faction.WOLVES);
        return;
      }
    }

    if (factionCount.get(Faction.WOLVES) == 0) {
      if (factionCount.get(Faction.NEUTRAL) >= factionCount.get(Faction.VILLAGERS)) {
        setWinner(Faction.NEUTRAL);
        return;
      }
    }
  }

  private void setWinner(Faction faction) {
    bot.sendMessage("The " + faction + " have won the game!");
    bot.transition(null);
    this.winner = faction;
    printPlayers();
    bot.setMode(WolfBot.channel, "-m");
  }

  private void printPlayers() {
    for (Player p : namePlayerMap.values()) {
      bot.sendMessage(p.getName() + " - " + p.getRole() + " (" + p.getRole().getFaction() + ")");
    }
  }

  private boolean isGameOver() {
    return getWinner() != null;
  }

  /**
   * Checks to see if the current day / night should end
   */
  private void checkEndOfTimePeriod() {
    for (Player player : getAlivePlayers()) {
      if (!player.getRole().isFinished()) {
        return;
      }
    }
    end();
  }

  private void assignRoles(Map<Class<? extends GameRole>, Integer> roleCountMap) throws Exception {
    List<Player> playersWhoNeedRoles = Lists.newArrayList(namePlayerMap.values());
    outerLoop: for (Entry<Class<? extends GameRole>, Integer> entry : roleCountMap.entrySet()) {
      for (int i = 0; i < entry.getValue(); i++) {
        if (playersWhoNeedRoles.isEmpty()) {
          break outerLoop;
        }
        Player randomPlayer =
            playersWhoNeedRoles.remove((int) (Math.random() * playersWhoNeedRoles.size()));
        GameRole role = entry.getKey().newInstance();
        role.setEngine(this);
        randomPlayer.setRole(role);
      }
    }

    bot.sendMessage("Assigning roles...");

    for (Player player : namePlayerMap.values()) {
      bot.sendMessage(player, "You are a " + player.getRole() + ".");
    }

    bot.sendMessage("Roles have been assigned.");
  }

  @Override
  public void onMessage(WolfBot bot, String channel, String sender, String login, String hostname,
      String message) {
    WolfBot.handleMessage(bot, actions, channel, sender, message);
  }

  @Override
  public void onPrivateMessage(WolfBot bot, String sender, String login, String hostname,
      String message) {
    Player player = getPlayer(sender);
    if (player == null) {
      throw new WolfException("You are not part of the game.");
    }

    if (!player.isAlive()) {
      throw new WolfException("You are dead.");
    }

    player.getRole().handlePrivateMessage(message);

    checkEndOfTimePeriod();
  }

  @Override
  public void onPart(WolfBot bot, String channel, String sender, String login, String hostname) {
    // Will need to handle a player leaving mid game.
  }

  public VotingHistory getVotingHistory() {
    return votingHistory;
  }

  public Player getPlayer(String sender) {
    return namePlayerMap.get(sender.toLowerCase());
  }

  public Iterable<Player> getAlivePlayers() {
    return Iterables.filter(namePlayerMap.values(), new Predicate<Player>() {
      @Override
      public boolean apply(Player player) {
        return player.isAlive();
      }
    });
  }

  public Iterable<Player> getAlivePlayers(final Class<? extends GameRole> role) {
    return Iterables.filter(getAlivePlayers(), new Predicate<Player>() {
      @Override
      public boolean apply(Player player) {
        return role.isAssignableFrom(player.getRole().getClass());
      }
    });
  }

  public Iterable<Player> getAlivePlayers(final Faction faction) {
    return Iterables.filter(getAlivePlayers(), new Predicate<Player>() {
      @Override
      public boolean apply(Player player) {
        return faction == player.getRole().getFaction();
      }
    });
  }

  public boolean isPlayerAlive(String playerName) {
    return false;
  }

  public Time getTime() {
    return time;
  }

  public WolfBot getBot() {
    return bot;
  }

  public Faction getWinner() {
    return winner;
  }

  private <T> T getProperty(String propertyName) {
    return properties.get(propertyName.toLowerCase()).getValue();
  }

  private void executeSpells() {
    List<Spell> toExecute = Lists.newArrayList(spells);
    this.spells.clear();

    Collections.sort(toExecute);

    for (Spell spell : toExecute) {
      spell.execute(this);
    }
  }

  public void cast(Spell spell) {
    if (spell == null) {
      throw new IllegalArgumentException("spell can't be null!");
    }

    spells.add(spell);
  }

}
