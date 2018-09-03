package eu.ha3.mc.haddon;

import java.io.File;

import eu.ha3.mc.haddon.implem.Instantiator;

/**
 * Basic utility interface for communicating with the game.
 */
public interface Utility {
	/**
	 * Register a Private Access getter on a certain name, that operates on a
	 * certain Class in a certain Object instance. The last two arguments are
	 * increasingly of priority: The rightmost argument is evaluated first.<br>
	 * When used by getPrivate(...):<br>
	 * The rightmost fieldname (if not null) is used first. If it doesn't work,
	 * the arguments left to it are used. If everything fails, it uses the
	 * zeroOffsets, that is the nth field (0th being the first field).
	 * zeroOffsets is ignored if it is a negative number. If none worked, this
	 * throws a PrivateAccessException containing the name of that getter.
	 *
	 * @param name
	 * @param classToPerformOn
	 * @param zeroOffsets
	 * @param lessToMoreImportantFieldName
	 */
    @Deprecated
	public void registerPrivateGetter(String name, Class<?> classToPerformOn, int zeroOffsets, String... lessToMoreImportantFieldName);

	/**
	 * Register a Private Access setter on a certain name, that operates on a
	 * certain Class in a certain Object instance. The last two arguments are
	 * increasingly of priority: The rightmost argument is evaluated first.<br>
	 * When used by getPrivate(...):<br>
	 * The rightmost fieldname (if not null) is used first. If it doesn't work,
	 * the arguments left to it are used. If everything fails, it uses the
	 * zeroOffsets, that is the nth field (0th being the first field).
	 * zeroOffsets is ignored if it is a negative number. If none worked, this
	 * throws a PrivateAccessException containing the name of that setter.
	 *
	 * @param name
	 * @param classToPerformOn
	 * @param zeroOffsets
	 * @param lessToMoreImportantFieldName
	 */
    @Deprecated
	public void registerPrivateSetter(String name, Class<?> classToPerformOn, int zeroOffsets, String... lessToMoreImportantFieldName);

	/**
	 * Gets a registered Private field
	 *
	 * @param instance
	 * @param name
	 * @return
	 * @throws PrivateAccessException
	 */
    @Deprecated
	public Object getPrivate(Object instance, String name) throws PrivateAccessException;

	/**
	 * Sets a registered Private field
	 *
	 * @param instance
	 * @param name
	 * @param value
	 * @throws PrivateAccessException
	 */
    @Deprecated
	public void setPrivate(Object instance, String name, Object value) throws PrivateAccessException;

	/**
	 * Checks it a given class exists in the current environment.
	 * @param className
	 * @return
	 */
	public boolean isPresent(String className);

	/**
	 * Creates a virtual constructor for the given class. Returns a dummy if the class does not exist.
	 * @param className
	 * @param parameters
	 */
	public <E> Instantiator<E> getInstantiator(String className, Class<?>... types);

	/**
	 * Returns the world height.<br/>
	 * <br/>
	 * There is no guarantee this method will work when no world is loaded. The
	 * implementation will attempt to make this value vary depending on the
	 * currently loaded world.
	 *
	 * @return World height
	 */
	public int getWorldHeight();

	/**
	 * Returns the Mods directory
	 *
	 * @return Mods directory
	 */
	public File getModsFolder();

	/**
	 * Returns the root Minecraft directory
	 *
	 * @return Mc directory
	 */
	public File getMcFolder();

	/**
	 * Gets the current GuiScreen.
	 * <p>
	 * No type information provided.
	 */
	public Object getCurrentScreen();

	/**
	 * Checks if the current gui screen matches the given type.
	 */
	public boolean isCurrentScreen(final Class<?> classtype);

	public void displayScreen(Object screen);

	/**
	 * Closes the current gui screen if there is one
	 */
	public void closeCurrentScreen();

	public Client getClient();

	/**
	 * Gets the total ticks that have passed on the client.
	 */
	public long getClientTick();

	public void pauseSounds(boolean pause);

	/**
	 * Checks if the game is paused.
	 * <p>
	 * i.e There are no guis currently open that pause the game, we are not on a lan server, nor on a dedicated server.
	 */
	public boolean isGamePaused();

	public boolean isSingleplayer();

	/**
	 * Prints a chat message.
	 */
	public void printChat(Object... args);

	/**
	 * Checks if all of the given keys are pressed.
	 */
	public boolean areKeysDown(int... args);

	/**
	 * Prepares a drawString sequence.
	 */
	public void prepareDrawString();

	/**
	 * Draws a string on-screen using viewport size percentages.<br>
	 * Alignment is a number between 1 and 9. It corresponds to the key position
	 * of a classic layout keyboard numpad. For instance, 7 means "top left",
	 * because the key "7" is at the top left.
	 *
	 * @param text	Text to print.
	 * @param px	X position
	 * @param py	Y position
	 * @param offx	Horizontal offset
	 * @param offy	Vertical offset
	 * @param alignment Number from 1 to 9 corresponding to numpad position on a
	 *            keyboard (not a phone).
	 * @param cr Red color 0-255
	 * @param cg Green color 0-255
	 * @param cb Blue color 0-255
	 * @param ca Alpha channel 0-255
	 * @param hasShadow	True to paint a shadow
	 */
	public void drawString(String text, float px, float py, int offx, int offy, char alignment, int cr, int cg, int cb, int ca, boolean hasShadow);

}
