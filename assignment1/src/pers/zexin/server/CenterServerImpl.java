package pers.zexin.server;

import pers.zexin.bean.*;
import pers.zexin.util.Tool;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CenterServerImpl implements CenterServer{
    HashMap<Character, List> recordMap = new HashMap<>();
    final private static Configuration configuration = new Configuration();
    private static Location location;
    int teacherRecordNum = 0;
    int studentRecordNum = 0;

    public static void startServer(Location locationPara) {
        location = locationPara;
        try{
            CenterServer centerServer = new CenterServerImpl();
            CenterServer stub =
                    (CenterServer) UnicastRemoteObject.exportObject(centerServer, getPort());
            Registry registry = LocateRegistry.createRegistry(getPort());
            registry.bind(location.toString(), stub);
            System.out.println(location.toString() + " Server ready.");
        }
        catch (Exception re) {
            System.out.println(re.toString());
        }
    }

    @Override
    public synchronized TeacherRecord createTRecord(String firstName, String lastName, String address, String phone, String specialization, Location location, Manager manager) {
        List<TeacherRecord> teacherRecordList = recordMap.get(lastName.charAt(0));
        if (teacherRecordList == null) {
            teacherRecordList = new LinkedList();
            recordMap.put(lastName.charAt(0), teacherRecordList);
        }
        TeacherRecord teacherRecord = new TeacherRecord(generateRecordId("TR"), firstName, lastName, address, phone
                , specialization, location);
        teacherRecordList.add(teacherRecord);
        teacherRecordNum++;
        generateLog("SUCCESS", manager.getManagerId(), "createTRecord: " + teacherRecord.toString());
        return teacherRecord;
    }

    @Override
    public synchronized boolean createSRecord(String firstName, String lastName, String[] courseRegistered, String status, String statusDate, Manager manager) {
        return false;
    }

    @Override
    public synchronized String getRecordCounts(Manager manager) {
        return "123";
    }

    @Override
    public synchronized boolean editRecord(String recordID, String fileName, String newValue, Manager manager) {
        return false;
    }

    public static int getPort() {
        return configuration.getPort();
    }

    /**
     *
     * @param prefiex "TR" or "SR"
     * @return
     */
    private String generateRecordId(String prefiex) {
        int id;
        if (prefiex.equals("TR")) {
            id = teacherRecordNum;
        } else {
            id = studentRecordNum;
        }
        for (int i = 0; i < 5 - String.valueOf(id).length(); i++) {
            prefiex += "0";
        }
        return prefiex + id;
    }

    private void generateLog(String status, String managerID, String operationaMessage) {
        String message = "Status: " + status + ", Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                + ", ManagerID: " + managerID + ", operation: " + operationaMessage;
        System.out.println(message);
        Tool.write2LogFile(message, configuration.getServerLogDirectory(), location.toString());
    }
}
