package eu.ha3.mc.haddon;

import java.util.List;

/*** An Identity that can be checked for updates. */
public interface UpdatableIdentity extends Identity {
    
    /** Returns a unique name for the thing. */
    public String getUniqueName();
    
    /***
     * Returns a list of URLs pointing to Forge Update Checker-style update JSONs.
     */
    public List<String> getUpdateURLs();
}
