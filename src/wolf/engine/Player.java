package wolf.engine;

import wolf.role.GameRole;

public class Player {

	private final String name;

	private GameRole role;

	private boolean alive = true;

	public Player(String name) {
		this.name = name;
	}

	public boolean isAlive() {
		return alive;
	}

	public void kill() {
		if (alive) {
			alive = false;
		} else {
			throw new RuntimeException(name + " is already dead.");
		}
	}

	public String getName() {
		return name;
	}

	public GameRole getRole() {
		return role;
	}

	public void setRole(GameRole role) {
		this.role = role;
		role.setPlayer(this);
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
