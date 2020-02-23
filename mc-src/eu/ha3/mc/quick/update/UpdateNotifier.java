package eu.ha3.mc.quick.update;

import java.io.InputStream;
import java.net.URL;

import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
	
	private int lastFound;
	
	private int displayCount = 3;
	private int displayRemaining = 0;
	private boolean enabled = true;
	
	public UpdateNotifier(NotifiableHaddon mod, String... queries) {
		haddon = mod;
		queryLocations = queries;
		lastFound = mod.getIdentity().getHaddonVersionNumber();
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
				// TODO fix update checker
				//e.printStackTrace();
			}
		}
	}
	
	private boolean checkUpdates(String queryLoc) throws Exception {
		final int currentVersionNumber = haddon.getIdentity().getHaddonVersionNumber();
		final String currentVersionType = haddon.getIdentity().getHaddonVersionPrefix();
		
		URL url = new URL(String.format(queryLoc, currentVersionNumber, currentVersionType));
		InputStream contents = url.openStream();
		
		int solvedVersion = 0;
		String solvedVersionType = "r";
		String solvedMinecraftVersion = "";
		String jasonString = IOUtils.toString(contents, "UTF-8");
		
		JsonObject jason = new JsonParser().parse(jasonString).getAsJsonObject();
		JsonArray versions = jason.get("versions").getAsJsonArray();
		for (JsonElement element : versions.getAsJsonArray()) {
			JsonObject o = element.getAsJsonObject();
			int vn = o.get("number").getAsInt();
			if (vn > solvedVersion) {
				solvedVersion = vn;
				if (o.has("for")) {
					solvedMinecraftVersion = o.get("for").getAsString();
				}
				if (o.has("type")) {
					solvedVersionType = o.get("type").getAsString();
				}
			}
		}
		
		log("Update version found: " + solvedVersion + " (running " + currentVersionNumber + ")");
		Thread.sleep(10000);
		
		if (solvedVersion > currentVersionNumber || (solvedVersion >= currentVersionNumber && !solvedVersionType.contentEquals(currentVersionType))) {
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
				int vc = solvedVersion - currentVersionNumber;
				reportUpdate(solvedMinecraftVersion, solvedVersionType, solvedVersion, vc == 0 ? 1 : vc);
				needsSave = true;
			}
			
			if (needsSave) {
				haddon.saveConfig();
			}
			
			return needsSave;
		}
		
		return false;
	}
	
	private void reportUpdate(String solvedMC, String solvedType, int solved, int count) {
		Chatter chatter = haddon.getChatter();
		if (solvedMC.equals("")) {
			chatter.printChat(TextFormatting.GOLD, "An update is available: ", solvedType + solved);
		} else if (solvedMC.equals(haddon.getIdentity().getHaddonMinecraftVersion())) {
			chatter.printChat(TextFormatting.GOLD, "An update is available for your version of Minecraft: ", solvedType + solved);
		} else {
			chatter.printChat(
					TextFormatting.GOLD, "An update is available for ",
					TextFormatting.GOLD, TextFormatting.ITALIC, "another",
					TextFormatting.GOLD, " version of Minecraft: ", solvedType + solved + " for " + solvedMC);
		}
		chatter.printChatShort(TextFormatting.GOLD, "You're ", TextFormatting.WHITE, count, TextFormatting.GOLD, " version" + (count > 1 ? "s" : "") + " late.");
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
		configuration.setProperty("update.version", haddon.getIdentity().getHaddonVersionNumber());
		configuration.setProperty("update.display.remaining", 0);
		configuration.setProperty("update.display.count", 3);
	}
	
	public void loadConfig(ConfigProperty configuration) {
		enabled = configuration.getBoolean("update.enabled");
		lastFound = configuration.getInteger("update.version");
		displayRemaining = configuration.getInteger("update.display.remaining");
		displayCount = configuration.getInteger("update.display.count");
	}
	
}
