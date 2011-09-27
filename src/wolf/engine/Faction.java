package wolf.engine;

public enum Faction {

	VILLAGE, WOLVES;

	@Override
	public String toString() {
		return this == VILLAGE ? "Village" : "Wolves";
	};

}
