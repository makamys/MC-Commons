package eu.ha3.mc.haddon.supporting;

import net.minecraft.client.gui.GuiScreen;

public interface SupportsGuiFrameEvents {
    /**
     * Triggered on each tick outside of a game while the gui tick events are hooked
     * onto the manager.
     * 
     * @param gui
     * @param semi Intra-tick time, from 0f to 1f
     */
    public void onGuiFrame(GuiScreen gui, float semi);
}
