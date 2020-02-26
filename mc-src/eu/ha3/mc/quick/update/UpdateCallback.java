package eu.ha3.mc.quick.update;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import eu.ha3.mc.haddon.implem.HaddonVersion;

@FunctionalInterface
public interface UpdateCallback {
    
    HaddonVersion decidePlatformVersion(Map<HaddonVersion, Pair<HaddonVersion, Map<String, String>>> latestPerPlatform);
    
}
