package eu.ha3.mc.haddon;

import net.minecraft.client.settings.KeyBinding;

/**
 * Interface for adding/removing a keybinging.
 *
 */
public interface Keyer {
    public void addKeyBinding(KeyBinding bind);

    public void removeKeyBinding(KeyBinding bind);
}