package eu.ha3.mc.haddon.implem;

import java.util.List;

import eu.ha3.mc.haddon.Identity;
import eu.ha3.mc.haddon.UpdatableIdentity;

public class UpdatableHaddonIdentity extends HaddonIdentity implements UpdatableIdentity {
    
    /*** Reserved unique name referring to the Haddon context. */
    public static final String MOD = "mod";
    
    protected final String uniqueName;
    protected final List<String> updateURLs;
    
    public UpdatableHaddonIdentity(Identity id, String uniqueName, List<String> updateURLs) {
        this(id.getHaddonName(), id.getHaddonVersion(), id.getHaddonMinecraftVersion(), id.getHaddonAddress(), uniqueName, updateURLs);
    }
    
    public UpdatableHaddonIdentity(String name, HaddonVersion version, String mc, String address, String uniqueName, List<String> updateURLs) {
        super(name, version, mc, address);
        this.uniqueName = uniqueName;
        this.updateURLs = updateURLs;
    }

    @Override
    public String getUniqueName() {
        return uniqueName;
    }

    @Override
    public List<String> getUpdateURLs() {
        return updateURLs;
    }
    
    @Override
    public String getPlatformName() {
        return "Minecraft";
    }

    @Override
    public HaddonVersion getPlatformVersion() {
        return new HaddonVersion(getHaddonMinecraftVersion());
    }

}
