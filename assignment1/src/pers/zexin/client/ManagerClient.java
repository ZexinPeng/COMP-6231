package pers.zexin.client;

import pers.zexin.bean.Configuration;
import pers.zexin.bean.Location;
import pers.zexin.bean.Manager;
import pers.zexin.bean.TeacherRecord;
import pers.zexin.server.CenterServer;
import pers.zexin.util.Tool;

import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagerClient {
    private static Configuration configuration = new Configuration();

    public static void startClient(Location location) {
        try {
            String registryURL = "rmi://" + configuration.getHost() + ":" + configuration.getPort() + "/LVL";
            // find the remote object and cast it to an interface object
            CenterServer centerServer = (CenterServer) Naming.lookup(registryURL);
            System.out.println("Lookup completed ");
            // invoke the remote method
            TeacherRecord teacherRecord = centerServer.createTRecord("zexin", "peng", "Shanghai Road"
                    , "15689477162", "computer", location, new Manager("LVL0001"));
            if (teacherRecord != null) {
                generateLog("SUCCESS", "LVL0001", teacherRecord.toString() , location.toString());
            }
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private static void generateLog(String status, String managerID, String operationaMessage, String location) {
        String message = "Status: " + status + ", Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                + ", ManagerID: " + managerID + ", operation: " + operationaMessage;
        System.out.println(message);
        Tool.write2LogFile(message, configuration.getServerLogDirectory(), location);
    }
}
