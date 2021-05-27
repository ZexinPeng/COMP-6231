package pers.zexin.bean;

import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private Properties properties;

    private int port;
    private String host;
    private String serverLogDirectory;
    private String clientLogDirectory;

    public Configuration() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            port = Integer.parseInt(properties.getProperty("port"));
            host = properties.getProperty("host");
            serverLogDirectory = properties.getProperty("serverLogDirectory");
            clientLogDirectory = properties.getProperty("clientLogDirectory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getServerLogDirectory() {
        return serverLogDirectory;
    }

    public String getClientLogDirectory() {
        return clientLogDirectory;
    }
}
