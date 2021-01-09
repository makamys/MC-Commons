package eu.ha3.util.property.simple;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map.Entry;
import java.util.Properties;

import eu.ha3.util.property.contract.ConfigInputStream;

public class InputStreamConfigProperty extends VersionnableProperty implements ConfigInputStream {

    @Override
    public boolean loadStream(InputStream stream) {
        try {
            Reader reader = new InputStreamReader(stream);

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
        return false;
    }
}
