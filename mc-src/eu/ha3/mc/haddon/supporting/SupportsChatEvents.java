package eu.ha3.mc.haddon.supporting;

import net.minecraft.util.text.ITextComponent;

public interface SupportsChatEvents {
    /**
     * Triggered when the OperatorChatter receives chat while it's enabled.
     */
    public void onChat(ITextComponent chat, String message);
}
