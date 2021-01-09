package eu.ha3.mc.haddon.supporting;

import net.minecraft.entity.player.EntityPlayer;

public interface SupportsPlayerFrameEvents {
    /**
     * Triggered on each frame for each player while the frame events are hooked
     * onto the manager.
     * 
     * @param semi Intra-tick time, from 0f to 1f
     */
    public void onFrame(EntityPlayer ply, float semi);
}
