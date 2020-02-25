package eu.ha3.mc.haddon.implem;

import eu.ha3.mc.haddon.Identity;

public class HaddonIdentity implements Identity {
	protected final String NAME;
	
	protected final HaddonVersion VERSION;
	protected final String MCVERSION;
	protected final String ADDRESS;
	
	public HaddonIdentity(String name, int version, String mc, String address) {
		this(name, new HaddonVersion(version), mc, address);
	}
	
   public HaddonIdentity(String name, HaddonVersion version, String mc, String address) {
        NAME = name;
        VERSION = version;
        MCVERSION = mc;
        ADDRESS = address;
    }
	
	@Override
	public String getHaddonName() {
		return NAME;
	}
	
	@Override
	public HaddonVersion getHaddonVersion() {
		return VERSION;
	}
	
	@Override
	public String getHaddonMinecraftVersion() {
		return MCVERSION.toString();
	}
	
	@Override
	public String getHaddonAddress() {
		return ADDRESS;
	}
	
	@Override
	public String getHaddonHumanVersion() {
		return getHaddonVersion() + " for " + getHaddonMinecraftVersion();
	}
}
