package util;

import java.io.IOException;
import java.util.Properties;

public class Configuration {
    private Properties properties;

    private String serverLogDirectory;
    private String clientLogDirectory;
    private int portLVL;
    private int portMTL;
    private int portDDO;
    private int studentPortID;
    private int teacherPortID;
    private String host;

    public Configuration() {
        properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
            portLVL = Integer.parseInt(properties.getProperty("LVLport"));
            portMTL = Integer.parseInt(properties.getProperty("MTLport"));
            portDDO = Integer.parseInt(properties.getProperty("DDOport"));
            studentPortID = Integer.parseInt(properties.getProperty("studentIDport"));
            teacherPortID = Integer.parseInt(properties.getProperty("teacherIDport"));
            serverLogDirectory = properties.getProperty("serverLogDirectory");
            clientLogDirectory = properties.getProperty("clientLogDirectory");
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

    public int getStudentPortID() {
        return studentPortID;
    }

    public int getTeacherPortID() {
        return teacherPortID;
    }

    public String getServerLogDirectory() {
        return serverLogDirectory;
    }

    public String getClientLogDirectory() {
        return clientLogDirectory;
    }

    public String getHost() {
        return host;
    }
}
