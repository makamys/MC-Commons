package eu.ha3.mc.haddon.implem;

import java.util.Arrays;
import java.util.stream.Collectors;

/***
 * A comparable version type, represented as a series of numbers separated by dots.
 * @author makamys
 *
 */
public class HaddonVersion implements Comparable<HaddonVersion>{
    
    private final int[] components;
    
    public HaddonVersion(String versionString) {
        this(Arrays.stream(versionString.split("\\.")).mapToInt(x -> Integer.parseInt(x)).toArray());
    }
    
    public HaddonVersion(int... components) {
        this.components = components;
    }

    @Override
    public int compareTo(HaddonVersion o) {
        int minLength = Math.min(components.length, o.components.length);
        
        for(int i = 0; i < minLength; i++) {
            if(components[i] < o.components[i]) {
                return -1;
            } else if(components[i] > o.components[i]) {
                return 1;
            }
        }
        return components.length > o.components.length ? 1 : -1; // e.g. 1.2.1 > 1.2
    }
    
    public int getComponent(int index) {
        return components[0];
    }
    
    public int getMajorVersion() {
        return getComponent(0);
    }
    
    @Override
    public String toString() {
        return String.join(".", Arrays.stream(components).boxed().map(x -> String.valueOf(x)).collect(Collectors.toList()));
    }
    
}
