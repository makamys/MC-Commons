package eu.ha3.util.property.simple;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;

import com.google.common.io.Files;

import eu.ha3.util.property.contract.ConfigSource;

public class ConfigProperty extends VersionnableProperty implements ConfigSource {
    private String path;

    private String globalDescription = "";

    private Map<String, String> descriptionMap = new HashMap<String, String>();

    @Override
    public void setSource(String source) {
        path = source;
    }

    @Override
    public boolean load() {
        File file = new File(path);
        if (file.exists()) {
            try {
                IOUtils.readLines(new FileReader(file)).forEach(l -> {
                    if (!l.startsWith("#") && !l.isEmpty()) {
                        if (l.contains("=")) {
                            String[] parts = l.split("=");
                            setProperty(parts[0], parts.length > 1 ? parts[1] : "");
                        } else {
                            LogManager.getLogger("haddon").warn("Ignoring malformed line in " + file.getName() + ": " + l);
                        }
                    }
                });
                commit();
                save(); // save to restore any potentially missing config options
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                revert();
            }
        } else { // create config file with default settings
            save();
        }
        return false;
    }

    @Override
    public boolean save() {
        try {
            File userFile = new File(this.path);
            Files.createParentDirs(userFile);

            try (FileWriter writer = new FileWriter(userFile)) {

                if (!globalDescription.isEmpty()) {
                    writeComment(globalDescription, writer);
                    writer.write("\n");
                }

                for (String k : getAllProperties().keySet().stream().sorted().collect(Collectors.toList())) {
                    String v = getString(k);

                    if (descriptionMap.containsKey(k)) {
                        writeComment(descriptionMap.get(k), writer);
                    }
                    writer.write(k + "=" + v + "\n\n");
                }
            } catch (IOException e) {
                LogManager.getLogger("haddon").error("Failed to save config: " + e);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void writeComment(String desc, Writer writer) throws IOException {
        for (String descriptionLine : desc.split("\n")) {
            writer.write("# " + descriptionLine + "\n");
        }
    }

    public void setProperty(String name, Object o, String description) {
        super.setProperty(name, o);

        descriptionMap.put(name, description);
    }

    public void setGlobalDescription(String globalDesc) {
        this.globalDescription = globalDesc != null ? globalDesc : "";
    }
}