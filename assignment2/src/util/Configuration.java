package util;

import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private Properties properties;

    private int portLVL;
    private int portMTL;
    private int portDDO;

    public Configuration() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            portLVL = Integer.parseInt(properties.getProperty("LVLport"));
            portMTL = Integer.parseInt(properties.getProperty("MTLport"));
            portDDO = Integer.parseInt(properties.getProperty("DDOport"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Properties getProperties() {
        return properties;
    }

    public int getPortLVL() {
        return portLVL;
    }

    public int getPortMTL() {
        return portMTL;
    }

    public int getPortDDO() {
        return portDDO;
    }
}
