package pers.zexin.client;

import pers.zexin.bean.*;
import pers.zexin.server.CenterServer;
import pers.zexin.util.Configuration;
import pers.zexin.util.Tool;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ManagerClient {
    private static Configuration configuration = new Configuration();
    private static Location location;

    public static void startCreateTRecordClient() {
        try {
            String registryURL = "rmi://" + configuration.getHost() + ":" + getRMIPort() + "/" + location;
            // find the remote object and cast it to an interface object
            CenterServer centerServer = (CenterServer) Naming.lookup(registryURL);
            System.out.println("get data from center server: " + location);
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

    public static void startCreateSRecordClient() {
        try {
            String registryURL = "rmi://" + configuration.getHost() + ":" + getRMIPort() + "/" + location;
            // find the remote object and cast it to an interface object
            CenterServer centerServer = (CenterServer) Naming.lookup(registryURL);
            System.out.println("get data from center server: " + location);
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

    public static void startGetRecordCounts() {
        // invoke the remote method
        String recordCounts = null;
        try {
            recordCounts = getStub().getRecordCounts();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (recordCounts != null) {
            System.out.println("[SUCCESS]" + " the amount of records is " + recordCounts);
        }
    }

    public static void startEditRecord(String recordID, String fieldName, String newValue) {
        try {
            String message = getStub().editRecord(recordID, fieldName, newValue, new Manager(configuration.getManagerID()));
            if (message == null || message.equals("")) {
                generateLog("[ERROR]", configuration.getManagerID(), "null message from the server");
            }
            System.out.println(message);
            Tool.write2LogFile(message, configuration.getClientLogDirectory(), configuration.getManagerID());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static CenterServer getStub() {
        String registryURL = "rmi://" + configuration.getHost() + ":" + getRMIPort() + "/" + location;
        CenterServer centerServer = null;
        // find the remote object and cast it to an interface object
        try {
            centerServer = (CenterServer) Naming.lookup(registryURL);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        System.out.println("get data from center server: " + location);
        return centerServer;
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

    private static int getRMIPort() {
        if (location.equals(Location.LVL)) {
            return configuration.getLVLrmiport();
        } else if (location.equals(Location.MTL)) {
            return configuration.getMTLrmiport();
        }
        return configuration.getDDOrmiport();
    }

    public static void initiate(String id, Location location) {
        setLocation(location);
        setManagerId(id);
    }

    private static void setManagerId(String id) {
        configuration.setManagerID(location + id);
    }

    private static void setLocation(Location currentLocation) {
        location = currentLocation;
    }
}
