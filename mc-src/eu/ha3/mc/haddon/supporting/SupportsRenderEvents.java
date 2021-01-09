package eu.ha3.mc.haddon.supporting;

import net.minecraft.client.gui.GuiScreen;

public interface SupportsRenderEvents {
    /**
     * Callback for when a frame is rendered.
     */
    public void onRender();

    /**
     * Callback for render frames whilst not in game.
     * 
     * @param currentScreen The active gui.
     */
    public void onRenderGui(GuiScreen currentScreen);

    /**
     * In-game render callback.
     */
    public void onRenderWorld();

    /**
     * Called immediately after the world/camera transform is initialised
     */
    public void onSetupCameraTransform();
}
