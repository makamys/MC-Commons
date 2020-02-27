package eu.ha3.mc.haddon.implem;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.profiler.Profiler;

public class ProfilerHelper {
    public static List<String> goToRoot(Profiler p) {
        List<String> profilerSections = new LinkedList<String>();
        List<String> stopIfReached = Arrays.asList("root", "[UNKNOWN]");
        String lastSection = "";
        while(!stopIfReached.contains(lastSection = p.getNameOfLastSection())) {
            profilerSections.add(lastSection);
            p.endSection();
        }
        
        return profilerSections;
    }
    
    public static void startNestedSection(Profiler p, List<String> sections) {
        for(String section : sections) {
            p.startSection(section);
        }
    }
}
