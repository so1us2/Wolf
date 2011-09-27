package wolf.engine;

import wolf.role.GameRole;

public class Player {

	private final String name;

	private GameRole role;

	public Player(String name) {
		this.name = name;
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

	public void begin(WolfEngine engine, Time time) {
		role.begin(engine, this, time);
	}

}
