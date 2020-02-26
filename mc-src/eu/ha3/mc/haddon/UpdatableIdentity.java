package eu.ha3.mc.haddon;

import java.util.List;

import eu.ha3.mc.haddon.implem.HaddonVersion;

/*** An Identity that can be checked for updates. */
public interface UpdatableIdentity extends Identity {
    
    /** Returns a unique name for the thing. */
    public String getUniqueName();
    
    /***
     * Returns a list of URLs pointing to Forge Update Checker-style update JSONs.
     */
    public List<String> getUpdateURLs();
    
    /***
     * Returns the platform version this is made for.
     * In practice this is used by the update notifer to check if the updates are made
     * for this version of the platform or another one.
     */
    public HaddonVersion getPlatformVersion();
    
    /***
     * Returns the human readable name of the platform this thing is for.
     */
    public String getPlatformName();
}
