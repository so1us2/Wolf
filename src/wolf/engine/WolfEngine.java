package wolf.engine;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import wolf.GameHandler;
import wolf.GameInitializer;
import wolf.WolfBot;
import wolf.WolfException;
import wolf.action.init.AbstractInitAction;
import wolf.engine.spell.KillSpell;
import wolf.engine.spell.Spell;
import wolf.role.GameRole;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;

public class WolfEngine implements GameHandler {

	private final List<AbstractInitAction> actions = Lists.newArrayList();

	private final WolfBot bot;

	private final Map<String, Player> namePlayerMap;
	private final Map<String, WolfProperty> properties;

	private VotingHistory votingHistory;

	private Time time;
	int dayNumber = 0;

	private Faction winner = null;

	private final List<Spell> spells = Lists.newArrayList();

	public WolfEngine(WolfBot bot, GameInitializer initializer) throws Exception {
		this.bot = bot;
		this.namePlayerMap = initializer.getNamePlayerMap();
		this.properties = initializer.getProperties();

		Time startingTime = getProperty(WolfProperty.STARTING_TIME);

		assignRoles(initializer.getRoleCountMap());

		begin(startingTime);
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
		} else {
			bot.sendMessage("The world grows dark as the villagers drift to sleep.");
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

	public void roleChat(Class<? extends GameRole> targetClass, Player sender, String message) {
		for (Player player : getAlivePlayers()) {
			if (!player.getName().equals(sender) && targetClass.isAssignableFrom(player.getRole().getClass())) {
				bot.sendMessage(player, "<WolfChat> " + sender + ": " + message);
			}
		}
	}

	private void checkForWinner() {
		Map<Faction, Integer> factionCount = Maps.newHashMap();

		for (Faction faction : Faction.values()) {
			factionCount.put(faction, 0);
		}

		for (Player player : getAlivePlayers()) {
			Faction faction = player.getRole().getFaction();
			Integer i = factionCount.get(faction);
			factionCount.put(faction, i + 1);
		}

		// see if there are more wolves than villagers
		if (factionCount.get(Faction.WOLVES) >= factionCount.get(Faction.VILLAGERS)) {
			setWinner(Faction.WOLVES);
			return;
		}

		// see if there are no more wolves left
		if (factionCount.get(Faction.WOLVES) == 0) {
			setWinner(Faction.VILLAGERS);
			return;
		}
	}

	private void setWinner(Faction faction) {
		bot.sendMessage("The " + faction + " have won the game!");
		bot.transition(null);
		this.winner = faction;
		printPlayers();
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
				Player randomPlayer = playersWhoNeedRoles.remove((int) (Math.random() * playersWhoNeedRoles.size()));
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
	public void onMessage(WolfBot bot, String channel, String sender, String login, String hostname, String message) {
		WolfBot.handleMessage(bot, actions, channel, sender, message);
	}

	@Override
	public void onPrivateMessage(WolfBot bot, String sender, String login, String hostname, String message) {
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

	public void wolfChat(String message) {

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
