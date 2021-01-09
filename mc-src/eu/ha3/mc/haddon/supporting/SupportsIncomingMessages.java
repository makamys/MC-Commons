package eu.ha3.mc.haddon.supporting;

import net.minecraft.network.play.client.CPacketCustomPayload;

public interface SupportsIncomingMessages {
    /**
     * Receives a message that has been enlisted for.
     */
    public void onIncomingMessage(CPacketCustomPayload message);
}
