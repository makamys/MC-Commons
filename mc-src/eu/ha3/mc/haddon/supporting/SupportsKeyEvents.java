package eu.ha3.mc.haddon.supporting;

import net.minecraft.client.settings.KeyBinding;

/**
 * Handler for the KeyWatcher that will be notified when a key's state changes.
 */
public interface SupportsKeyEvents {
    /**
     * Called when a key's state changes.
     * 
     * @param event The key that changed.
     */
    public void onKey(KeyBinding event);
}
