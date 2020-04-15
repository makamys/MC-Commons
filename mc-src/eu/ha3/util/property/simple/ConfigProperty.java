package eu.ha3.util.property.simple;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;

import com.google.common.io.Files;

import eu.ha3.util.property.contract.ConfigSource;

public class ConfigProperty extends VersionnableProperty implements ConfigSource {
	private String path;
	
	@Override
	public void setSource(String source) {
		path = source;
	}
	
	@Override
	public boolean load() {
		File file = new File(path);
		if (file.exists()) {
			try {
				Reader reader = new FileReader(file);
				
				Properties props = new Properties();
				props.load(reader);
				
				for (Entry<Object, Object> entry : props.entrySet()) {
					setProperty(entry.getKey().toString(), entry.getValue().toString());
				}
				commit();
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
			Properties props = new Properties() {
				@Override
				public synchronized Enumeration<Object> keys() {
					return Collections.enumeration(new TreeSet<Object>(super.keySet()));
				}
			};
			for (Entry<String, String> property : getAllProperties().entrySet()) {
				props.setProperty(property.getKey(), property.getValue());
			}
			props.store(new FileWriter(userFile), "");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
}
