package wolf.action;

import java.util.List;

import wolf.WolfBot;

public abstract class BotAction {

	private int minArguments, maxArguments;

	public BotAction() {
		this(0);
	}

	public BotAction(int numArguments) {
		this.minArguments = numArguments;
		this.maxArguments = numArguments;
	}

	public boolean matches(String command) {
		return this.getCommandName().toLowerCase().startsWith(command.toLowerCase());
	}

	public abstract String getCommandName();

	public void tryInvoke(WolfBot bot, String sender, String command, List<String> args) {
		if (!hasPermission(sender)) {
			throw new RuntimeException("Permission denied.");
		}

		if (args.size() < minArguments || args.size() > maxArguments) {
			throw new RuntimeException("Invalid arguments.");
		}

		execute(bot, sender, command, args);
	}

	protected abstract void execute(WolfBot bot, String sender, String command, List<String> args);

	public boolean hasPermission(String sender) {
		if (requiresAdmin()) {
			return WolfBot.admins.contains(sender.toLowerCase());
		}
		return true;
	}

	protected boolean requiresAdmin() {
		return false;
	}

	public int getMinArguments() {
		return minArguments;
	}

	public int getMaxArguments() {
		return maxArguments;
	}

}
