package eu.ha3.mc.haddon;

public interface ChatWatcher {
    /**
     * Enable the functionality of (SupportsChatEvents)onChat() while in-game-world.
     * onChat will not run if the Haddon is not an instance of SupportsChatEvents.
     */
    public void setChatEnabled(boolean enabled);
}
