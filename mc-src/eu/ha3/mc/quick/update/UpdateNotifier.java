package eu.ha3.mc.quick.update;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import eu.ha3.mc.haddon.implem.HaddonVersion;
import eu.ha3.mc.quick.chat.Chatter;
import eu.ha3.util.property.simple.ConfigProperty;

/**
 * The Update Notifier.
 * 
 * @author Hurry
 * 
 */
public class UpdateNotifier extends Thread implements Updater {
	
	private final NotifiableHaddon haddon;
	private final String[] queryLocations;
	
	private HaddonVersion lastFound;
	
	private int displayCount = 3;
	private int displayRemaining = 0;
	private boolean enabled = true;
	
	public UpdateNotifier(NotifiableHaddon mod, String... queries) {
		haddon = mod;
		queryLocations = queries;
		lastFound = mod.getIdentity().getHaddonVersion();
	}
	
	public void attempt() {
		if (enabled) start();
	}
	
	@Override
	public void run() {
		for (String i : queryLocations) {
			try {
				if (checkUpdates(i)) return;
			} catch (Exception e) {
				log("Exception whilst checking update location: " + i);
				//e.printStackTrace();
			}
		}
	}
	
	private boolean checkUpdates(String queryLoc) throws Exception {
	    HaddonVersion currentVersion = haddon.getIdentity().getHaddonVersion();
	    
		URL url = new URL(queryLoc);
		InputStream contents = url.openStream();
		
		HaddonVersion solvedVersion = null;
		String solvedMinecraftVersion = "";
		String jasonString = IOUtils.toString(contents, "UTF-8");
		
		JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
		
		JsonElement versionsElem = jason.get(haddon.getIdentity().getHaddonMinecraftVersion());
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
		Thread.sleep(10000);
		
		if (solvedVersion.compareTo(currentVersion) > 0) {
			ConfigProperty config = haddon.getConfig();
			
			boolean needsSave = false;
			if (solvedVersion != lastFound) {
				lastFound = solvedVersion;
				displayRemaining = displayCount;
				
				needsSave = true;
				config.setProperty("update.version", lastFound);
				config.setProperty("update.display.remaining", displayRemaining);
			}
			
			if (displayRemaining > 0) {
				config.setProperty("update.display.remaining", --displayRemaining);
				int vc = solvedVersion.getMajorVersion() - currentVersion.getMajorVersion();
				reportUpdate(solvedMinecraftVersion, solvedVersion, vc);
				needsSave = true;
			}
			
			if (needsSave) {
				haddon.saveConfig();
			}
			
			return needsSave;
		}
		
		return false;
	}
	
	private void reportUpdate(String solvedMC, HaddonVersion solved, int count) {
		Chatter chatter = haddon.getChatter();
		if (solvedMC.equals("")) {
			chatter.printChat(TextFormatting.GOLD, "An update is available: ", solved);
		} else if (solvedMC.equals(haddon.getIdentity().getHaddonMinecraftVersion())) {
			chatter.printChat(TextFormatting.GOLD, "An update is available for your version of Minecraft: ", solved);
		} else {
			chatter.printChat(
					TextFormatting.GOLD, "An update is available for ",
					TextFormatting.GOLD, TextFormatting.ITALIC, "another",
					TextFormatting.GOLD, " version of Minecraft: ", solved + " for " + solvedMC);
		}
		if(count > 0) {
		    chatter.printChatShort(TextFormatting.GOLD, "You're ", TextFormatting.WHITE, count, TextFormatting.GOLD, " major version" + (count > 1 ? "s" : "") + " late.");
		}
		chatter.printChatShort(TextFormatting.UNDERLINE, new ClickEvent(ClickEvent.Action.OPEN_URL, haddon.getIdentity().getHaddonAddress()), haddon.getIdentity().getHaddonAddress());
		
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
		System.out.println("(UN: " + haddon.getIdentity().getHaddonName() + ") " + mess);
	}
	
	public void fillDefaults(ConfigProperty configuration) {
		configuration.setProperty("update.enabled", true);
		configuration.setProperty("update.version", haddon.getIdentity().getHaddonVersion());
		configuration.setProperty("update.display.remaining", 0);
		configuration.setProperty("update.display.count", 3);
	}
	
	public void loadConfig(ConfigProperty configuration) {
		enabled = configuration.getBoolean("update.enabled");
		lastFound = new HaddonVersion(configuration.getString("update.version"));
		displayRemaining = configuration.getInteger("update.display.remaining");
		displayCount = configuration.getInteger("update.display.count");
	}
	
}
