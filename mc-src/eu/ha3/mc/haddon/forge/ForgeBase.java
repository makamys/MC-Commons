package eu.ha3.mc.haddon.forge;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.profiler.Profiler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Timer;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.event.world.BlockEvent.PlaceEvent;

import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.logging.log4j.Logger;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.implem.HaddonUtilityImpl;
import eu.ha3.mc.haddon.implem.ProfilerHelper;
import eu.ha3.mc.haddon.supporting.SupportsInGameChangeEvents;
import eu.ha3.mc.haddon.supporting.SupportsBlockChangeEvents;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsPlayerFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsSoundEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;
import eu.ha3.mc.haddon.supporting.event.BlockChangeEvent;
import eu.ha3.mc.haddon.supporting.SupportsBlockChangeEvents.ClickType;

public class ForgeBase implements OperatorCaster {
    private static Logger logger;

    protected final Haddon haddon;
    protected final boolean shouldTick;
    protected final boolean suTick;
    protected final boolean suFrame;
    protected final boolean suFrameP;
    protected final boolean suInGame;
    protected final boolean suSound;
    protected final boolean suBlockChange;

    protected int tickCounter;
    protected boolean enableTick;
    protected boolean enableFrame;

    private int ticksSinceLastRender = 0;
    private boolean wasInGame;

    private Queue<BlockChangeEvent> blockEventQueue = new LinkedBlockingQueue<>();

    public ForgeBase(Haddon haddon) {
        this.haddon = haddon;
        suTick = haddon instanceof SupportsTickEvents;
        suFrame = haddon instanceof SupportsFrameEvents;
        suFrameP = haddon instanceof SupportsPlayerFrameEvents;
        suInGame = haddon instanceof SupportsInGameChangeEvents;
        suBlockChange = haddon instanceof SupportsBlockChangeEvents;
        suSound = haddon instanceof SupportsSoundEvents;

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
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent event) {
        ticksSinceLastRender++;
    }

    @SubscribeEvent
    public void onRenderTick(RenderTickEvent event) {
        boolean clock = ticksSinceLastRender > 0;
        ticksSinceLastRender = 0;

        Minecraft mc = Minecraft.getMinecraft();

        float partialTicks = mc.isGamePaused() ? mc.renderPartialTicksPaused : mc.getRenderPartialTicks();

        Entity renderViewEntity = mc.getRenderViewEntity();
        boolean inGame = renderViewEntity != null && renderViewEntity.world != null;

        onTickLiteLoaderStyle(mc, partialTicks, inGame, clock);
    }

    @SubscribeEvent
    public void onSoundEvent(PlaySoundEvent event) {
        if (!shouldTick)
            return;

        if (suSound) {
            if (!((SupportsSoundEvents) haddon).onSound(event.getSound(), event.getName(), event.getManager())) {
                event.setResultSound(null);
            }
        }
    }

    private void onTickLiteLoaderStyle(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
        if (inGame != wasInGame) {
            if (suInGame) {
                ((SupportsInGameChangeEvents) haddon).onInGameChange(inGame);
            }
        }
        wasInGame = inGame;

        if (!shouldTick || !inGame)
            return;

        Profiler p = Minecraft.getMinecraft().profiler;
        List<String> profilerSections = ProfilerHelper.goToRoot(p);
        p.startSection(haddon.getIdentity().getHaddonName());

        if (enableTick && clock) {
            if (suTick) {
                ((SupportsTickEvents) haddon).onTick();
            }
            tickCounter++;
        }

        if (suBlockChange) {
            while (!blockEventQueue.isEmpty()) {
                ((SupportsBlockChangeEvents) haddon).onBlockChanged(blockEventQueue.remove());
            }

        }

        if (enableFrame) {
            if (suFrame) {
                ((SupportsFrameEvents) haddon).onFrame(partialTicks);
            }
            if (suFrameP) {
                for (EntityPlayer ply : haddon.getUtility().getClient().getAllPlayers()) {
                    if (ply != null)
                        ((SupportsPlayerFrameEvents) haddon).onFrame(ply, partialTicks);
                }
            }
        }

        p.endSection();
        ProfilerHelper.startNestedSection(p, profilerSections);
    }

    @EventHandler
    public void init(FMLInitializationEvent event, String modid, String name, String version) {
        MinecraftForge.EVENT_BUS.register(this);

        haddon.onLoad();
    }

    @SubscribeEvent
    public void onBlock(BlockEvent event) {
        if (!shouldTick || Minecraft.getMinecraft().world == null)
            return;

        if (suBlockChange) {
            Block oldBlock = null, newBlock = null;
            if (event instanceof PlaceEvent) {
                oldBlock = Block.getBlockById(0);
                newBlock = ((PlaceEvent) event).getPlacedBlock().getBlock();
            } else if (event instanceof BreakEvent) {
                oldBlock = event.getState().getBlock();
                newBlock = Minecraft.getMinecraft().world.getBlockState(event.getPos()).getBlock();
            }
            if (oldBlock != null) {
                // this is on server side! we must send it to client side and process it there
                blockEventQueue.add(new BlockChangeEvent(event.getPos().getX(), event.getPos().getY(),
                        event.getPos().getZ(), oldBlock, newBlock));
            }
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
