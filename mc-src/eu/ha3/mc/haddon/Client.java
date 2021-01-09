package eu.ha3.mc.haddon;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.entity.player.EntityPlayer;

public interface Client extends Keyer {
    /**
     * Gets the current player.
     */
    public EntityPlayer getPlayer();

    /**
     * Gets all players visible from this client.
     */
    public List<EntityPlayer> getAllPlayers();

    /**
     * Gets the client font renderer.
     */
    public FontRenderer getFontRenderer();

    /**
     * Adds a reload listener to the client's resourcepack manager.
     */
    public boolean registerReloadListener(IResourceManagerReloadListener reloadListener);

}
