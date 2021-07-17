package test.client;

import main.factory.ConfigurationFactory;
import main.util.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    protected static main.webservice.lvl.CenterServer LVLServer = new main.webservice.lvl.ServerImplService().getServerImplPort();
    protected static main.webservice.mtl.CenterServer MTLServer = new main.webservice.mtl.ServerImplService().getServerImplPort();
    protected static main.webservice.ddo.CenterServer DDOServer = new main.webservice.ddo.ServerImplService().getServerImplPort();

    protected String managerID;

    public static void main(String[] args) {
        System.out.println(LVLServer.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), "LVL0001"));
        System.out.println(MTLServer.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), "LVL0001"));
        System.out.println(DDOServer.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), "LVL0001"));
        System.out.println(LVLServer.getRecordCounts("LVL0001"));
        System.out.println(MTLServer.getRecordCounts("LVL0001"));
        System.out.println(DDOServer.getRecordCounts("LVL0001"));
    }

    private static void generateLog(String status, String managerID, String operationaMessage) {
        String message;
        if (status.equals("[ERROR]")) {
            message = status + " something goes wrong in the server.";
        }
        else {
            message = status + " date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    + ", managerID: " + managerID + ", operation: " + operationaMessage;
        }
        System.out.println(message);
        Tool.write2LogFile(message, ConfigurationFactory.getConfiguration().getClientLogDirectory(), managerID);
    }

    protected void generateLog(String message) {
        System.out.println(message);
        Tool.write2LogFile(message, ConfigurationFactory.getConfiguration().getClientLogDirectory(), managerID);
    }

    private void setManagerID(String location, int num) {
        StringBuilder sb = new StringBuilder(location);
        for (int i = 0; i < 4 - String.valueOf(num).length(); i++) {
            sb.append("0");
        }
        managerID = sb.append(num).toString();
    }
}
