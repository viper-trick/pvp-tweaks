package net.minecraft;

/**
 * The version components of Minecraft that is used for identification in
 * save games.
 */
public record SaveVersion(int id, String series) {
	/**
	 * The default series of a version, {@code main}, if a series is not specified.
	 */
	public static final String MAIN_SERIES = "main";

	public boolean isNotMainSeries() {
		return !this.series.equals("main");
	}

	/**
	 * {@return whether this save version can be loaded by the {@code other} version}
	 */
	public boolean isAvailableTo(SaveVersion other) {
		return SharedConstants.OPEN_INCOMPATIBLE_WORLDS ? true : this.series().equals(other.series());
	}
}
