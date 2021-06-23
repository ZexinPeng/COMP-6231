package util;

import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private Properties properties;

    public Configuration() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
