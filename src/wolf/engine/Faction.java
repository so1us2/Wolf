package wolf.engine;

public enum Faction {

	VILLAGERS, WOLVES;

	@Override
	public String toString() {
		return this == VILLAGERS ? "Villagers" : "Wolves";
	};

}
