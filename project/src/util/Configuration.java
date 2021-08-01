package util;

public class Configuration {
    private static final String HOST = "localhost";
    private static final int HEARTBEAT_PERIOD = 3000;

    private static final String separator = ";";

    private static final int LVL_PORT = 8888;
    private static final int MTL_PORT = 8889;
    private static final int DDO_PORT=8890;
    private static final int STUDENT_ID_PORT = 8891;
    private static final int TEACHER_ID_PORT = 8892;
    private static final int TransPortPortLVL = 8893;
    private static final int TransPortPortMTL = 8894;
    private static final int TransPortPortDDO = 8895;

    private static final int LVL_REPLICATION_PORTS = 8896;
    private static final int MTL_REPLICATION_PORTS = 8897;
    private static final int DDO_REPLICATION_PORTS = 8898;

    private static final int[] LVL_HEARTBEAT_PORTS = new int[]{9000, 9001, 9002};
    private static final int[] MTL_HEARTBEAT_PORTS = new int[]{9003, 9004, 9005};
    private static final int[] DDO_HEARTBEAT_PORTS = new int[]{9006, 9007, 9008};

//
    // log configuration
    private static final String SERVER_LOG_DIRECTORY= "./logs/server/";
    private static final String CLIENT_LOG_DIRECTORY= "./logs/client/";
    private static final String MANAGER_ID = "LVL0001";

    public static String getHost() {
        return HOST;
    }

    public static int getLvlPort() {
        return LVL_PORT;
    }

    public static int getMtlPort() {
        return MTL_PORT;
    }

    public static int getDdoPort() {
        return DDO_PORT;
    }

    public static String getServerLogDirectory() {
        return SERVER_LOG_DIRECTORY;
    }

    public static String getClientLogDirectory() {
        return CLIENT_LOG_DIRECTORY;
    }

    public static String getManagerId() {
        return MANAGER_ID;
    }

    public static int getStudentIdPort() {
        return STUDENT_ID_PORT;
    }

    public static int getTeacherIdPort() {
        return TEACHER_ID_PORT;
    }

    public static int getLvlReplicationPort() {
        return LVL_REPLICATION_PORTS;
    }

    public static int getMtlReplicationPort() {
        return MTL_REPLICATION_PORTS;
    }

    public static int getDdoReplicationPort() {
        return DDO_REPLICATION_PORTS;
    }

    public static int getHeartbeatPeriod() {
        return HEARTBEAT_PERIOD;
    }

    public static int[] getLvlHeartbeatPorts() {
        return LVL_HEARTBEAT_PORTS;
    }

    public static int[] getMtlHeartbeatPorts() {
        return MTL_HEARTBEAT_PORTS;
    }

    public static int[] getDdoHeartbeatPorts() {
        return DDO_HEARTBEAT_PORTS;
    }

    public static String getSeparator() {
        return separator;
    }
}
