package pers.zexin.client;

import pers.zexin.bean.*;
import pers.zexin.server.CenterServer;
import pers.zexin.util.Tool;

import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagerClient {
    private static Configuration configuration = new Configuration();
    private static Location location;

    public static void startCreateTRecordClient(Location locationPara) {
        location = locationPara;
        try {
            String registryURL = "rmi://" + configuration.getHost() + ":" + getPort() + "/LVL";
            // find the remote object and cast it to an interface object
            CenterServer centerServer = (CenterServer) Naming.lookup(registryURL);
            System.out.println("get data from center server: LVL");
            // invoke the remote method
            TeacherRecord teacherRecord = centerServer.createTRecord("zexin", "peng", "Shanghai Road"
                    , "15689477162", "computer", location, new Manager(configuration.getManagerID()));
            if (teacherRecord != null) {
                generateLog("[SUCCESS]", configuration.getManagerID(), "createTRecord: " + teacherRecord.toString());
            }
            else {
                generateLog("[ERROR]", null, null);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void startCreateSRecordClient(Location locationPara) {
        location = locationPara;
        try {
            String registryURL = "rmi://" + configuration.getHost() + ":" + getPort() + "/LVL";
            // find the remote object and cast it to an interface object
            CenterServer centerServer = (CenterServer) Naming.lookup(registryURL);
            System.out.println("get data from center server: LVL");
            // invoke the remote method
            StudentRecord studentRecord = centerServer.createSRecord("zexin", "peng"
                    , new String[]{"maths", "french"}, "active", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    , new Manager(configuration.getManagerID()));
            if (studentRecord != null) {
                generateLog("[SUCCESS]", configuration.getManagerID(), "createSRecord: " + studentRecord.toString());
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void startGetRecordCounts(Location locationPara) {
        location = locationPara;
        try {
            String registryURL = "rmi://" + configuration.getHost() + ":" + getPort() + "/LVL";
            // find the remote object and cast it to an interface object
            CenterServer centerServer = (CenterServer) Naming.lookup(registryURL);
            System.out.println("get data from center server: LVL");
            // invoke the remote method
            String recordCounts = centerServer.getRecordCounts();
            if (recordCounts != null) {
                System.out.println("[SUCCESS]" + " the amount of records is " + recordCounts);
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
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
        Tool.write2LogFile(message, configuration.getClientLogDirectory(), configuration.getManagerID());
    }

    private static int getPort() {
        if (location.equals(Location.LVL)) {
            return configuration.getPortLVL();
        } else if (location.equals(Location.MTL)) {
            return configuration.getPortMTL();
        }
        return configuration.getPortDDO();
    }
}
