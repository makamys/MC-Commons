package eu.ha3.mc.haddon.forge;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

import org.apache.logging.log4j.Logger;

import eu.ha3.mc.haddon.forge.mixin.IMinecraft;
import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.implem.HaddonUtilityImpl;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsPlayerFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;


public class ForgeBase implements OperatorCaster
{
    private static Logger logger;
    
    protected final Haddon haddon;
    protected final boolean shouldTick;
    protected final boolean suTick;
    protected final boolean suFrame;
    protected final boolean suFrameP;
    
    protected int tickCounter;
    protected boolean enableTick;
    protected boolean enableFrame;
    
    private int ticksSinceLastRender = 0;
    
    public ForgeBase(Haddon haddon) {
        if(MixinLoaderForge.hasInitializedSuccessfully()) {
            this.haddon = haddon;
            suTick = haddon instanceof SupportsTickEvents;
            suFrame = haddon instanceof SupportsFrameEvents;
            suFrameP = haddon instanceof SupportsPlayerFrameEvents;
            
            shouldTick = suTick || suFrame;
            
            haddon.setUtility(new HaddonUtilityImpl() {
                @Override
                public long getClientTick() {
                    return getTicks();
                }
            });
            haddon.setOperator(this);
        } else {
            this.haddon = null;
            shouldTick = suTick = suFrame = suFrameP = false;
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        logger = event.getModLog();
    }
    
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        ticksSinceLastRender++;
    }
    
    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event)
    {
        boolean clock = ticksSinceLastRender > 0;
        ticksSinceLastRender = 0;
        
        Minecraft mc = Minecraft.getMinecraft();
        
        float partialTicks = mc.isGamePaused() ? ((IMinecraft)mc).renderPartialTicksPaused() : mc.getRenderPartialTicks();
        
        Entity renderViewEntity = mc.getRenderViewEntity();
        boolean inGame = renderViewEntity != null && renderViewEntity.world != null;
        
        onTickLiteLoaderStyle(mc, partialTicks, inGame, clock);
    }
    
    private void onTickLiteLoaderStyle(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (!shouldTick || !inGame) return;
        if (enableTick && clock) {
            if (suTick) {
                ((SupportsTickEvents)haddon).onTick();
            }
            tickCounter++;
        }
        if (enableFrame) {
            if (suFrame) {
                ((SupportsFrameEvents)haddon).onFrame(partialTicks);
            }
            if (suFrameP) {
                for (EntityPlayer ply : haddon.getUtility().getClient().getAllPlayers()) {
                    if (ply != null) ((SupportsPlayerFrameEvents)haddon).onFrame(ply, partialTicks);
                }
            }
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event, String modid, String name, String version)
    {   
        if(MixinLoaderForge.hasInitializedSuccessfully()) {
            MinecraftForge.EVENT_BUS.register(this);
            
            haddon.onLoad();
        } else {
            System.out.println("Cancelled loading " + name + " (" + modid + " " + version + "), because the Haddon mixins failed to load.");
        }
    }

    @Override
    public void setTickEnabled(boolean enabled) {
        enableTick = enabled;
    }

    @Override
    public boolean getTickEnabled() {
        return enableTick;
    }

    @Override
    public void setFrameEnabled(boolean enabled) {
        enableFrame = enabled;
    }

    @Override
    public boolean getFrameEnabled() {
        return enableFrame;
    }

    @Override
    public int getTicks() {
        return tickCounter;
    }
}
