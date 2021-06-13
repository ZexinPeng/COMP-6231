package pers.zexin.util;

import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private Properties properties;

    private String host;
    private String serverLogDirectory;
    private String clientLogDirectory;
    private String managerID;
    private int portLVL;
    private int portMTL;
    private int portDDO;
    private int LVLrmiport;
    private int MTLrmiport;
    private int DDOrmiport;

    public Configuration() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            portLVL = Integer.parseInt(properties.getProperty("LVLport"));
            portMTL = Integer.parseInt(properties.getProperty("MTLport"));
            portDDO = Integer.parseInt(properties.getProperty("DDOport"));
            LVLrmiport = Integer.parseInt(properties.getProperty("LVLrmiport"));
            MTLrmiport = Integer.parseInt(properties.getProperty("MTLrmiport"));
            DDOrmiport = Integer.parseInt(properties.getProperty("DDOrmiport"));
            host = properties.getProperty("host");
            serverLogDirectory = properties.getProperty("serverLogDirectory");
            clientLogDirectory = properties.getProperty("clientLogDirectory");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getLVLrmiport() {
        return LVLrmiport;
    }

    public int getMTLrmiport() {
        return MTLrmiport;
    }

    public int getDDOrmiport() {
        return DDOrmiport;
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

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }
}
