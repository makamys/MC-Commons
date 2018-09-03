package eu.ha3.mc.haddon;

import java.io.File;

import eu.ha3.mc.haddon.implem.Instantiator;

/**
 * Basic utility interface for communicating with the game.
 */
public interface Utility {

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
