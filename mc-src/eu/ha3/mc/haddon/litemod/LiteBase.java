package eu.ha3.mc.haddon.litemod;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import com.mumfrey.liteloader.InitCompleteListener;
import com.mumfrey.liteloader.LiteMod;
import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.haddon.OperatorCaster;
import eu.ha3.mc.haddon.implem.HaddonUtilityImpl;
import eu.ha3.mc.haddon.supporting.SupportsFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsInGameChangeEvents;
import eu.ha3.mc.haddon.supporting.SupportsPlayerFrameEvents;
import eu.ha3.mc.haddon.supporting.SupportsTickEvents;

public class LiteBase implements LiteMod, Tickable, InitCompleteListener, OperatorCaster {
	protected final Haddon haddon;
	protected final boolean shouldTick;
	protected final boolean suTick;
	protected final boolean suFrame;
	protected final boolean suFrameP;
	protected final boolean suInGame;
	
	protected int tickCounter;
	protected boolean enableTick;
	protected boolean enableFrame;
	
	private boolean wasInGame;
	
	public LiteBase(Haddon haddon) {
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
	
	@Override
	public String getName() {
		return haddon.getIdentity().getHaddonName();
	}
	
	@Override
	public String getVersion() {
		return haddon.getIdentity().getHaddonHumanVersion();
	}
	
	@Override
	public void onInitCompleted(Minecraft minecraft, LiteLoader loader) {
		haddon.onLoad();
	}
	
	@Override
	public void init(File configPath) {
	}
	
	@Override
	public void upgradeSettings(String version, File configPath, File oldConfigPath) {
	}
	
	@Override
	public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock) {
	    if(inGame != wasInGame) {
            if(suInGame) {
                ((SupportsInGameChangeEvents)haddon).onInGameChange(inGame);
            }
        }
        wasInGame = inGame;
        
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
	
	@Override
	public void setTickEnabled(boolean enabled) {
		enableTick = enabled;
	}
	
	@Override
	public void setFrameEnabled(boolean enabled) {
		enableFrame = enabled;
	}
	
	@Override
	public int getTicks() {
		return tickCounter;
	}

	@Override
	public boolean getTickEnabled() {
		return enableTick;
	}

	@Override
	public boolean getFrameEnabled() {
		return enableFrame;
	}
}
