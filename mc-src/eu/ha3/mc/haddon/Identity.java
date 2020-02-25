package eu.ha3.mc.haddon;

import eu.ha3.mc.haddon.implem.HaddonVersion;

public interface Identity {
	/**
	 * Returns the name of the mod. This can include spaces. Do not include the version number.
	 */
	public String getHaddonName();
	
	/**
	 * Returns the version. Format must be usable for automaton (to compare numbers).
	 */
	public HaddonVersion getHaddonVersion();
	
	/**
	 * Returns Minecraft version this is made for. Format is arbitrary, and
	 * should not be used for automaton.
	 */
	public String getHaddonMinecraftVersion();
	
	/**
	 * Returns a web URL for the website of this haddon.
	 */
	public String getHaddonAddress();
	
	/**
	 * Returns a human-readable version. Format is arbitrary, and should not be
	 * used for automaton.
	 */
	public String getHaddonHumanVersion();
}
