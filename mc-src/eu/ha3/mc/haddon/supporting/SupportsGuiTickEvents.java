package eu.ha3.mc.haddon.supporting;

import net.minecraft.client.gui.GuiScreen;

public interface SupportsGuiTickEvents {
    /**
     * Triggered on each tick outside of a game while the gui tick events are hooked
     * onto the manager.
     */
    public void onGuiTick(GuiScreen gui);
}
