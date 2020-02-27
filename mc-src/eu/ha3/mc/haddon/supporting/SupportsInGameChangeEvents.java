package eu.ha3.mc.haddon.supporting;

public interface SupportsInGameChangeEvents {
    /**
     * Triggered when the player enters a world from the menu, or exits to the menu.
     * More precisely, triggered when this tick is in-game while the last one was in
     * the menu, or vice-versa.
     */
    public void onInGameChange(boolean inGame);
}
