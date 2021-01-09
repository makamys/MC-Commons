package eu.ha3.mc.convenience;

/**
 * Basic action handler for key events.
 */
public interface Ha3KeyActions {
    /**
     * Action to perform before a key is fully pressed.
     */
    void doBefore();

    /**
     * Action to perform whilst a key is pressed.
     * 
     * @param curTime Total ticks that the key has been pressed.
     */
    void doDuring(int curTime);

    /**
     * Action to perform after a key is released.
     * 
     * @param curTime Total ticks that the key has been pressed.
     */
    void doAfter(int curTime);

}
