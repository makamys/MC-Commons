package eu.ha3.mc.haddon.supporting;

public interface SupportsFrameEvents {
    /**
     * Triggered on each frame while the frame events are hooked onto the manager.
     * 
     * @param semi Intra-tick time, from 0f to 1f
     */
    public void onFrame(float semi);
}
