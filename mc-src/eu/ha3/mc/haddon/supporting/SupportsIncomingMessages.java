package eu.ha3.mc.haddon.supporting;

import net.minecraft.network.play.client.C17PacketCustomPayload;

public interface SupportsIncomingMessages {
	/**
	 * Receives a message that has been enlisted for.
	 */
	public void onIncomingMessage(C17PacketCustomPayload message);
}
