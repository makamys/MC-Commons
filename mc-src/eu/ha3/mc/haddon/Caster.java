package eu.ha3.mc.haddon;

/**
 * This is an interface used by operators that manages tick and frame flow.
 * 
 * @author Hurry
 * 
 */
public interface Caster {
    /**
     * Enable the functionality of (SupportsTickEvents)onTick() while in-game-world.
     * onTick will not run if the Haddon is not an instance of SupportsTickEvents,
     * however, the number of ticks elapsed should still be counted as long as the
     * functionality is enabled.
     */
    public void setTickEnabled(boolean enabled);

    public boolean getTickEnabled();

    /**
     * Enable the functionality of (SupportsFrameEvents)onFrame() while
     * in-game-world. onFrame will not run if the Haddon is not an instance of
     * SupportsFrameEvents.
     */
    public void setFrameEnabled(boolean enabled);

    public boolean getFrameEnabled();

    /**
     * Returns the number of ticks elapsed while ticks are enabled. This works even
     * if the Haddon is not an instance of SupportsTickEvents.
     */
    public int getTicks();
}
