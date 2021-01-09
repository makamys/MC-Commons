package eu.ha3.mc.convenience;

import java.util.HashMap;

import net.minecraft.client.settings.KeyBinding;

public class Ha3KeyManager {
    HashMap<KeyBinding, Ha3KeyBinding> keys;

    public Ha3KeyManager() {
        keys = new HashMap<KeyBinding, Ha3KeyBinding>();
    }

    public void addKeyBinding(KeyBinding mckeybinding, Ha3KeyActions keyactions) {
        keys.put(mckeybinding, new Ha3KeyBinding(mckeybinding, keyactions));
    }

    public void handleKeyDown(KeyBinding event) {
        if (keys.containsKey(event)) {
            keys.get(event).handleBefore();
        }
    }

    public void handleRuntime() {
        for (Ha3KeyBinding i : keys.values()) {
            i.handle();
        }
    }

}
