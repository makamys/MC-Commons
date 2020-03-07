package eu.ha3.mc.haddon.supporting;

import net.minecraft.util.IChatComponent;

public interface SupportsChatEvents {
	/**
	 * Triggered when the OperatorChatter receives chat while it's enabled.
	 */
	public void onChat(IChatComponent chat, String message);
}
