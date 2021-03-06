package eu.ha3.mc.quick.update;

import eu.ha3.mc.haddon.Haddon;
import eu.ha3.mc.quick.chat.HasChatter;
import eu.ha3.mc.quick.configurable.HasConfiguration;

/**
 * Haddon that may recieve notifications.
 */
public interface NotifiableHaddon extends Haddon, HasChatter, HasConfiguration {
}
