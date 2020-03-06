package eu.ha3.mc.haddon.forge;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;

import java.util.List;

import org.apache.logging.log4j.Logger;

import eu.ha3.mc.haddon.forge.mixin.IMinecraft;
import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.implem.HaddonUtilityImpl;
import eu.ha3.mc.haddon.implem.ProfilerHelper;
import eu.ha3.mc.haddon.supporting.SupportsInGameChangeEvents;
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
    protected final boolean suInGame;
    
    protected int tickCounter;
    protected boolean enableTick;
    protected boolean enableFrame;
    
    private int ticksSinceLastRender = 0;
    private boolean wasInGame;
    
    public ForgeBase(Haddon haddon) {
        this.haddon = haddon;
        suTick = haddon instanceof SupportsTickEvents;
        suFrame = haddon instanceof SupportsFrameEvents;
        suFrameP = haddon instanceof SupportsPlayerFrameEvents;
        suInGame = haddon instanceof SupportsInGameChangeEvents;
        
        shouldTick = suTick || suFrame;
        
        haddon.setUtility(new HaddonUtilityImpl() {
            @Override
            public long getClientTick() {
                return getTicks();
            }
        });
        haddon.setOperator(this);
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
        if(inGame != wasInGame) {
            if(suInGame) {
                ((SupportsInGameChangeEvents)haddon).onInGameChange(inGame);
            }
        }
        wasInGame = inGame;
        
        if (!shouldTick || !inGame) return;
        
        Profiler p = Minecraft.getMinecraft().profiler;
        List<String> profilerSections = ProfilerHelper.goToRoot(p);
        p.startSection(haddon.getIdentity().getHaddonName());
        
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
        
        p.endSection();
        ProfilerHelper.startNestedSection(p, profilerSections);
    }

    @EventHandler
    public void init(FMLInitializationEvent event, String modid, String name, String version)
    {   
        MinecraftForge.EVENT_BUS.register(this);
        
        haddon.onLoad();
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
