package util;

public class Configuration {
    private static final String HOST = "localhost";
    private static final int LVL_PORT = 8888;
    private static final int MTL_PORT = 8889;
    private static final int DDO_PORT=8890;
    private static final int STUDENT_ID_PORT = 8891;
    private static final int TEACHER_ID_PORT = 8892;
    private static final int TransPortPortLVL = 8893;
    private static final int TransPortPortMTL = 8894;
    private static final int TransPortPortDDO = 8895;
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
}
