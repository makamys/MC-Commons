package eu.ha3.mc.convenience;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.client.settings.KeyBinding;
import eu.ha3.mc.haddon.Keyer;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;

public class Ha3KeyManager_2 implements SupportsTickEvents {
	private final Keyer keyer;
	private Map<KeyBinding, Ha3KeyActions> keys = new HashMap<KeyBinding, Ha3KeyActions>();
	private Map<KeyBinding, Integer> states = new HashMap<KeyBinding, Integer>();
	
	public Ha3KeyManager_2(Keyer keyer) {
		this.keyer = keyer;
	}
	
	public Ha3KeyManager_2() {
		this(null);
	}
	
	public void addKeyBinding(KeyBinding bind, Ha3KeyActions keyActions) {
		if (keyer != null) {
			keyer.addKeyBinding(bind);
		}
		keys.put(bind, keyActions);
		states.put(bind, 0);
	}
	
	@Override
	public void onTick() {
		for (KeyBinding bind : keys.keySet()) {
			if (bind.getIsKeyPressed())
			{
				int oldVal = states.get(bind);
				states.put(bind, oldVal + 1);
				
				if (oldVal == 0) {
					keys.get(bind).doBefore();
				} else {
					keys.get(bind).doDuring(oldVal);
				}
			} else {
				int state = states.get(bind);
				if (state > 0) {
					keys.get(bind).doAfter(state);
					states.put(bind, 0);
				}
			}
		}
	}
}
