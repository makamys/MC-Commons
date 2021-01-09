package eu.ha3.mc.haddon.implem;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.profiler.Profiler;

public class ProfilerHelper {
    public static List<String> goToRoot(Profiler p) {
        if (!p.profilingEnabled)
            return null;

        List<String> profilerSections = Arrays.asList(p.getNameOfLastSection().split("\\."));
        if (!profilerSections.isEmpty()) {
            profilerSections = profilerSections.subList(1, profilerSections.size());
        }

        List<String> stopIfReached = Arrays.asList("root", "[UNKNOWN]");

        while (!stopIfReached.contains(p.getNameOfLastSection())) {
            p.endSection();
        }

        return profilerSections;
    }

    public static void startNestedSection(Profiler p, List<String> sections) {
        if (!p.profilingEnabled)
            return;

        for (String section : sections) {
            p.startSection(section);
        }
    }
}
