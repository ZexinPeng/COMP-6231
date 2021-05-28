package pers.zexin.bean;

import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private Properties properties;

    private int port;
    private String host;
    private String serverLogDirectory;
    private String clientLogDirectory;
    private String managerID;
    private int portLVL=8888;
    private int portMTL=8889;
    private int portDDO=8890;

    public Configuration() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            port = Integer.parseInt(properties.getProperty("port"));
            portLVL = Integer.parseInt(properties.getProperty("LVLport"));
            portMTL = Integer.parseInt(properties.getProperty("MTLport"));
            portDDO = Integer.parseInt(properties.getProperty("DDOport"));
            host = properties.getProperty("host");
            serverLogDirectory = properties.getProperty("serverLogDirectory");
            clientLogDirectory = properties.getProperty("clientLogDirectory");
            managerID = properties.getProperty("managerID");
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

    public String getManagerID() {return managerID;}

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
