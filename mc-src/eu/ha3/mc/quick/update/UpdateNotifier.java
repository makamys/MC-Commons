package eu.ha3.mc.quick.update;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import org.apache.commons.io.IOUtils;

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
	
	boolean hasRun;
	
	private int displayCount = 3;
	private boolean enabled = true;
	
	public UpdateNotifier(NotifiableHaddon mod, String... queries) {
	    notifiableHaddon = mod;
		
		addJob(new UpdatableHaddonIdentity(mod.getIdentity(), UpdatableHaddonIdentity.MOD, new ArrayList<String>(Arrays.asList(queries))));
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
	
	public void addJob(UpdatableIdentity identity) {
	    if(canBeUpdateChecked(identity)) {
    	    if(!thingsUpdateChecked.contains(identity)) {
    	        thingsUpdateChecked.add(identity);
    	        thingsToUpdateCheck.add(identity);
    	    }
	    }
	    if(!thingsToUpdateCheck.isEmpty() && hasRun) {
	        attempt();
	    }
	}
	
	private void checkUpdates(UpdatableIdentity id) {
	    for (String url : id.getUpdateURLs()) {
            try {
                if (checkUpdates(id, url)) return;
            } catch (Exception e) {
                log("Exception whilst checking update location: " + url);
                //e.printStackTrace();
            }
        }
	}
	
	private boolean checkUpdates(UpdatableIdentity id, String queryLoc) throws Exception {
	    log("Checking " + id.getUniqueName() + " for updates");
	    
	    HaddonVersion currentVersion = id.getHaddonVersion();
	    
		URL url = new URL(queryLoc);
		InputStream contents = url.openStream();
		
		HaddonVersion solvedVersion = null;
		String solvedMinecraftVersion = "";
		String jasonString = IOUtils.toString(contents, "UTF-8");
		
		JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
		
		JsonElement versionsElem = jason.get(notifiableHaddon.getIdentity().getHaddonMinecraftVersion());
		if(versionsElem == null) { // no versions for this MC version
		    log("Update JSON contains no entries for current Minecraft version. Malformed JSON file?");
		    return false;
		} else {
		    JsonObject versions = versionsElem.getAsJsonObject();
		    List<HaddonVersion> availableVersions = versions.entrySet().stream()
		            .map(name -> new HaddonVersion(name.getKey())).collect(Collectors.toList());
		    solvedVersion = Collections.max(availableVersions); 
		}
		
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
	
	private synchronized void reportUpdate(UpdatableIdentity id, String solvedMC, HaddonVersion solved, int count, int displayRemaining) {
		Chatter chatter = notifiableHaddon.getChatter();
		if (solvedMC.equals("")) {
			chatter.printChat(TextFormatting.GOLD, "An update is available for " + id.getHaddonName() + ": ", solved);
		} else if (solvedMC.equals(id.getHaddonMinecraftVersion())) {
			chatter.printChat(TextFormatting.GOLD, "An update is available for " + id.getHaddonName() + " for your version of Minecraft: ", solved);
		} else {
			chatter.printChat(
					TextFormatting.GOLD, "An update is available for " + id.getHaddonName(),
					TextFormatting.GOLD, TextFormatting.ITALIC, "another",
					TextFormatting.GOLD, " version of Minecraft: ", solved + " for " + solvedMC);
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
