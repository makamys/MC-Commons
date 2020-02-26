package eu.ha3.mc.quick.update;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.ha3.mc.haddon.UpdatableIdentity;
import eu.ha3.mc.haddon.implem.HaddonVersion;
import eu.ha3.mc.haddon.implem.UpdatableHaddonIdentity;
import eu.ha3.mc.quick.chat.Chatter;
import eu.ha3.util.property.simple.ConfigProperty;
import eu.ha3.util.property.simple.PropertyException;

/**
 * The Update Notifier.
 * 
 * @author Hurry
 * 
 */
public class UpdateNotifier implements Updater {
	
	private final NotifiableHaddon notifiableHaddon;
	
	/*** Job queue for the update checker thread */
	private Queue<UpdatableIdentity> thingsToUpdateCheck = new LinkedList<UpdatableIdentity>();
	
	/*** Things that have already been update checked in this Minecraft session, and should not be checked again */
	private Set<UpdatableIdentity> thingsUpdateChecked = new HashSet<UpdatableIdentity>();
	
	private Map<UpdatableIdentity, UpdateCallback> updateCallbacks = new HashMap<>();
	
	boolean hasRun;
	
	private int displayCount = 3;
	private boolean enabled = true;
	
	public UpdateNotifier(NotifiableHaddon mod, String... queries) {
	    notifiableHaddon = mod;
		
		addJob(new UpdatableHaddonIdentity(mod.getIdentity(), UpdatableHaddonIdentity.MOD, new ArrayList<String>(Arrays.asList(queries))),
		        new HaddonVersion(mod.getIdentity().getHaddonMinecraftVersion()));
	}
	
	public void attempt() {
		if (enabled) {
		    Queue<UpdatableIdentity> batch = thingsToUpdateCheck;
		    thingsToUpdateCheck = new LinkedList<UpdatableIdentity>();
		    new Thread(() -> checkForUpdates(batch)).start();
		    
		    hasRun = true;
		}
	}
	
	private boolean canBeUpdateChecked(UpdatableIdentity identity) {
	    return identity.getUniqueName() != null && !identity.getUniqueName().equals("") &&
	            identity.getHaddonVersion() != null &&
	            identity.getUpdateURLs() != null && !identity.getUpdateURLs().isEmpty();
	}
	
	private void checkForUpdates(Queue<UpdatableIdentity> batch) {
	    for(UpdatableIdentity id : batch) {
	        checkUpdates(id);
	    }
	}
	
	/***
	 * Add the job of checking identity for updates. Any platform version will be matched.
	 */
	public void addJob(UpdatableIdentity identity) {
	    addJob(identity, HaddonVersion.NO_VERSION);
	}
	
	/***
     * Add the job of checking identity for updates. Only the platform version of allowedPlatformVersion will be matched.
     */
	public void addJob(UpdatableIdentity identity, HaddonVersion allowedPlatformVersion) {
	    addJob(identity, (Map<HaddonVersion, Pair<HaddonVersion, Map<String, String>>> p) -> {return allowedPlatformVersion;});
	}
	
	/***
     * Add the job of checking identity for updates. cb will be called to decide which platform version should be matched.
     */
	public void addJob(UpdatableIdentity identity, UpdateCallback cb) {
	    if(canBeUpdateChecked(identity)) {
    	    if(!thingsUpdateChecked.contains(identity)) {
    	        thingsUpdateChecked.add(identity);
    	        updateCallbacks.put(identity, cb);
    	        thingsToUpdateCheck.add(identity);
    	    }
	    }
	    if(!thingsToUpdateCheck.isEmpty() && hasRun) {
	        attempt();
	    }
	}
	
	private void checkUpdates(UpdatableIdentity id) {
	    UpdateCallback cb = updateCallbacks.remove(id);
	    for (String url : id.getUpdateURLs()) {
            try {
                if (checkUpdates(id, url, cb)) return;
            } catch (Exception e) {
                log("Exception whilst checking update location: " + url);
                //e.printStackTrace();
            }
        }
	}
	
	private Pair<HaddonVersion, HaddonVersion> solveVersion(String queryLoc, UpdateCallback cb) throws Exception {
        URL url = new URL(queryLoc);
        InputStream contents = url.openStream();
        
        HaddonVersion solvedVersion = null;
	    
	    String jasonString = IOUtils.toString(contents, "UTF-8");
        
        JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
        
        Map<HaddonVersion, Pair<HaddonVersion, Map<String, String>>> latestPerPlatformVer = new HashMap<>();
        
        for(Entry<String, JsonElement> e : jason.entrySet()) {
            if(Arrays.asList("homepage", "promos").contains(e.getKey())) continue;
            if(!e.getValue().isJsonObject()) {
                log("Ignoring unknown element in update JSON: " + e.getKey());
                continue;
            }
            
            String platform = e.getKey();
            
            JsonObject versions = e.getValue().getAsJsonObject();
            List<HaddonVersion> availableVersions = versions.entrySet().stream()
                    .map(name -> new HaddonVersion(name.getKey())).collect(Collectors.toList());
            HaddonVersion latest = Collections.max(availableVersions);
            
            latestPerPlatformVer.put(new HaddonVersion(platform), Pair.of(latest, null));
        }
        
        HaddonVersion requiredPlatformVer = cb.decidePlatformVersion(latestPerPlatformVer);
        
        if(requiredPlatformVer == null || requiredPlatformVer.equals(HaddonVersion.NO_VERSION)) { // "Don't care"; choose the newest
            requiredPlatformVer = Collections.max(latestPerPlatformVer.keySet());
        }
        
        if(latestPerPlatformVer.containsKey(requiredPlatformVer)) {
            solvedVersion = latestPerPlatformVer.get(requiredPlatformVer).getLeft();
        } else { // no versions for this platform version
            log("Update JSON contains no entries for " + requiredPlatformVer.toString() + ". Malformed JSON file?");
            return null;
        }
        
        return Pair.of(solvedVersion, requiredPlatformVer);
	}
	
	private boolean checkUpdates(UpdatableIdentity id, String queryLoc, UpdateCallback cb) throws Exception {
	    log("Checking " + id.getUniqueName() + " for updates");
	    
	    HaddonVersion currentVersion = id.getHaddonVersion();
		
		Pair<HaddonVersion, HaddonVersion> solved = solveVersion(queryLoc, cb);
		if(solved == null) return false;
		
		HaddonVersion solvedVersion = solved.getLeft();
        HaddonVersion solvedMinecraftVersion = solved.getRight();
		
		log("Update version found: " + solvedVersion + " (running " + currentVersion + ")");
		Thread.sleep(1000); // XXX why is this here?
		
		if (solvedVersion.compareTo(currentVersion) > 0) {
			ConfigProperty config = notifiableHaddon.getConfig();
			
			// doing fillDefaults()'s job for things that are not the mod itself (e.g. resource packs)
			HaddonVersion lastFound = id.getHaddonVersion();
			int displayRemaining = 0;
			
			try {
			    HaddonVersion newLastFound = new HaddonVersion(config.getString("update." + id.getUniqueName() + ".version"));
			    int newDisplayRemaining = config.getInteger("update." + id.getUniqueName() + ".display.remaining");
			    lastFound = newLastFound;
			    displayRemaining = newDisplayRemaining;
			} catch(PropertyException e) {};
			
			boolean needsSave = false;
			if (!solvedVersion.equals(lastFound)) {
				lastFound = solvedVersion;
				displayRemaining = displayCount;
				
				needsSave = true;
				config.setProperty("update." + id.getUniqueName() + ".version", lastFound);
				config.setProperty("update." + id.getUniqueName() + ".display.remaining", displayRemaining);
			}
			
			if (displayRemaining > 0) {
				config.setProperty("update." + id.getUniqueName() + ".display.remaining", --displayRemaining);
				int vc = solvedVersion.getMajorVersion() - currentVersion.getMajorVersion();
				reportUpdate(id, solvedMinecraftVersion, solvedVersion, vc, displayRemaining);
				needsSave = true;
			}
			
			if (needsSave) {
				notifiableHaddon.saveConfig();
			}
			
			return needsSave;
		}
		
		return false;
	}
	
	private synchronized void reportUpdate(UpdatableIdentity id, HaddonVersion solvedMC, HaddonVersion solved, int count, int displayRemaining) {
		Chatter chatter = notifiableHaddon.getChatter();
		if (solvedMC.equals(HaddonVersion.NO_VERSION)) {
			chatter.printChat(TextFormatting.GOLD, "An update is available for " + id.getHaddonName() + ": ver ", solved);
		} else if (solvedMC.equals(id.getPlatformVersion())) {
			chatter.printChat(TextFormatting.GOLD, "An update is available for " + id.getHaddonName() + " for your version of " + id.getPlatformName() + ": ver ", solved);
		} else {
			chatter.printChat(TextFormatting.GOLD, "An update is available for " + id.getHaddonName());
			chatter.printChatShort(TextFormatting.GOLD, "for ", TextFormatting.GOLD, TextFormatting.ITALIC, "another",
					                TextFormatting.GOLD, " version of " + id.getPlatformName() + ": ");
			chatter.printChatShort(TextFormatting.GOLD, "ver ", solved, TextFormatting.GOLD, " for ver ", solvedMC, TextFormatting.GOLD, ".");
		}
		if(count > 0) {
		    chatter.printChatShort(TextFormatting.GOLD, "You're ", TextFormatting.WHITE, count, TextFormatting.GOLD, " major version" + (count > 1 ? "s" : "") + " late.");
		}
		chatter.printChatShort(TextFormatting.UNDERLINE, new ClickEvent(ClickEvent.Action.OPEN_URL, id.getHaddonAddress()), id.getHaddonAddress());
		
		if (displayRemaining > 0) {
			chatter.printChatShort(
					TextFormatting.GRAY, "This message will display ",
					TextFormatting.WHITE, displayRemaining,
					TextFormatting.GRAY, " more time" + (displayRemaining > 1 ? "s" : "") + ".");
		} else {
			chatter.printChatShort(TextFormatting.GRAY, "You won't be notified anymore unless a newer version comes out.");
		}
	}
	
	private void log(String mess) {
		System.out.println("(UN: " + notifiableHaddon.getIdentity().getHaddonName() + ") " + mess);
	}
	
	public void fillDefaults(ConfigProperty configuration) {
		configuration.setProperty("update.enabled", true);
		configuration.setProperty("update." + UpdatableHaddonIdentity.MOD + ".version",
		        notifiableHaddon.getIdentity().getHaddonVersion());
		configuration.setProperty("update." + UpdatableHaddonIdentity.MOD + ".display.remaining", 0);
		configuration.setProperty("update.display.count", 3);
	}
	
	public void loadConfig(ConfigProperty configuration) {
		enabled = configuration.getBoolean("update.enabled");
		displayCount = configuration.getInteger("update.display.count");
	}
	
}
