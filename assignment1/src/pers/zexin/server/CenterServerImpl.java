package pers.zexin.server;

import pers.zexin.bean.*;
import pers.zexin.util.Tool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.*;

public class CenterServerImpl implements CenterServer{
    private static HashMap<Character, List<Record>> recordMap = new HashMap<>();
    final private static Configuration configuration = new Configuration();
    private static Location location;
    private static int teacherRecordNum = 0;
    private static int studentRecordNum = 0;

    public static void startServer(Location locationPara) {
        if (locationPara == null) {
            Tool.printError("the location of the server should be indicated!");
        }
        location = locationPara;
        startCountThread();
        initiate();
        try{
            CenterServer centerServer = new CenterServerImpl();
            CenterServer stub =
                    (CenterServer) UnicastRemoteObject.exportObject(centerServer, getPort());
            Registry registry = LocateRegistry.createRegistry(getPort());
            registry.bind(location.toString(), stub);
            System.out.println(location.toString() + " Server is ready.");
        }
        catch (Exception re) {
            System.out.println(re.toString());
        }
    }

    @Override
    public synchronized TeacherRecord createTRecord(String firstName, String lastName, String address, String phone, String specialization, Location location, Manager manager) {
        List<Record> teacherRecordList = recordMap.get(lastName.charAt(0));
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
    public synchronized StudentRecord createSRecord(String firstName, String lastName, String[] courseRegistered, String status, String statusDate, Manager manager) {
        List<Record> recordList = recordMap.get(lastName.charAt(0));
        if (recordList == null) {
            recordList = new LinkedList();
            recordMap.put(lastName.charAt(0), recordList);
        }
        StudentRecord studentRecord = new StudentRecord(generateRecordId("SR"), firstName, lastName, courseRegistered, status, statusDate);
        recordList.add(studentRecord);
        studentRecordNum++;
        generateLog("[SUCCESS]", manager.getManagerId(), "createSRecord: " + studentRecord.toString());
        return studentRecord;
    }

    @Override
    public synchronized String getRecordCounts() {
        int numLVL = 0, numMTL = 0, numDDO = 0;
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            byte[] m = new byte[4];
            InetAddress aHost = InetAddress.getByName(configuration.getHost());
            int[] portArr = new int[]{configuration.getPortLVL(), configuration.getPortDDO(), configuration.getPortMTL()};
            int ignoredPort;
            if (location.equals(Location.LVL)) {
                ignoredPort = configuration.getPortLVL();
                numLVL = studentRecordNum + teacherRecordNum;
            } else if (location.equals(Location.DDO)) {
                ignoredPort = configuration.getPortDDO();
                numDDO = studentRecordNum + teacherRecordNum;
            } else {
                ignoredPort = configuration.getPortMTL();
                numMTL = studentRecordNum + teacherRecordNum;
            }
            for (int i = 0; i < portArr.length; i++) {
                if (portArr[i] == ignoredPort) {
                    continue;
                }
                DatagramPacket request =
                        new DatagramPacket(m, 1, aHost, portArr[i]);
                aSocket.send(request);
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                if (portArr[i] == configuration.getPortLVL()) {
                    numLVL = Tool.bytes2Int(reply.getData());
                } else if (portArr[i] == configuration.getPortDDO()) {
                    numDDO = Tool.bytes2Int(reply.getData());
                } else {
                    numMTL = Tool.bytes2Int(reply.getData());
                }
            }
        }catch (SocketException e){System.out.println(e);
        }catch (IOException e){System.out.println(e);
        }finally {if(aSocket != null) aSocket.close();}
        return "MTL " + numMTL + ", LVL " + numLVL + ", DDO " + numDDO;
    }

    @Override
    public synchronized String editRecord(String recordID, String fieldName, String newValue, Manager manager) {
        for (Character key: recordMap.keySet()) {
            List<Record> recordList = recordMap.get(key);
            for (Record record: recordList) {
                if (record.getRecordID().equals(recordID)) {
                    if (record instanceof StudentRecord) {
                        if (fieldName.equals("courseRegistered")) {
                            return editCourseRegistered((StudentRecord) record, newValue, manager);
                        } else if (fieldName.equals("status")) {
                            return editStatus((StudentRecord) record, newValue, manager);
                        } else if (fieldName.equals("statusDate")) {
                            return editStatusDate((StudentRecord) record, newValue, manager);
                        } else {
                            return generateLog("[ERROR]", manager.getManagerId(), " fieldName [" + fieldName + " ] is not allowed to modify");
                        }
                    } else if (record instanceof TeacherRecord) {
                        if (fieldName.equals("address")) {
                            return editAddress((TeacherRecord) record, newValue, manager);
                        } else if (fieldName.equals("phone")) {
                            return editPhone((TeacherRecord) record, newValue, manager);
                        } else if (fieldName.equals("location")) {
                            return editLocation((TeacherRecord) record, newValue, manager);
                        } else {
                            return generateLog("[ERROR]", manager.getManagerId(), " fieldName [" + fieldName + " ] is not allowed to modify");
                        }
                    } else {
                        Tool.printError("wrong type: " + record.getClass().getName());
                    }
                }
            }
        }
        return generateLog("[ERROR]", manager.getManagerId(), "recordID [" + recordID + "] does not exist.");
    }

    public static int getPort() {
        if (location.equals(Location.LVL)) {
            return configuration.getPortLVL();
        } else if (location.equals(Location.MTL)) {
            return configuration.getPortMTL();
        }
        return configuration.getPortDDO();
    }

    /**
     *
     * @param prefiex "TR" or "SR"
     * @return
     */
    private static String generateRecordId(String prefiex) {
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

    private String generateLog(String status, String managerID, String operationaMessage) {
        String message;
        if (status.equals("[ERROR]")) {
            message = status + operationaMessage;
        }
        else {
            message = status + " date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    + ", managerID: " + managerID + ", operation: " + operationaMessage;
        }
        System.out.println(message);
        Tool.write2LogFile(message, configuration.getServerLogDirectory(), location.toString());
        return message;
    }

    /**
     * start a new thread to listen
     */
    private static void startCountThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int port;
                if (location.equals(Location.LVL)) {
                    port = configuration.getPortLVL();
                } else if (location.equals(Location.DDO)) {
                    port = configuration.getPortDDO();
                } else {
                    port = configuration.getPortMTL();
                }
                DatagramSocket aSocket = null;
                try{
                    aSocket = new DatagramSocket(port);
                    // create socket at agreed port
                    byte[] buffer = new byte[1000];
                    while(true){
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                        aSocket.receive(request);
                        DatagramPacket reply = new DatagramPacket(Tool.int2ByteArray(teacherRecordNum + studentRecordNum), 4,
                                request.getAddress(), request.getPort());
                        aSocket.send(reply);
                    }
                }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
                }catch (IOException e) {System.out.println("IO: " + e.getMessage());
                }finally {if(aSocket != null) aSocket.close();}
            }
        }).start();
    }

    private String editCourseRegistered(StudentRecord studentRecord, String newValue, Manager manager) {
        String[] oldValue = studentRecord.getCoursesRegistered();
        studentRecord.setCoursesRegistered(newValue.split(","));
        return generateLog("[SUCCESS]", manager.getManagerId(), generateCourseRegisteredMessage(studentRecord.getRecordID(), oldValue, newValue.split(",")));
    }

    private String generateCourseRegisteredMessage(String recordID, String[] oldValue, String[] newValue) {
        StringBuilder osb = new StringBuilder();
        for (int i = 0; i < oldValue.length - 1; i++) {
            osb.append(oldValue[i]);
            osb.append(", ");
        }
        osb.append(oldValue[oldValue.length - 1]);
        StringBuilder nsb = new StringBuilder();
        for (int i = 0; i < newValue.length - 1; i++) {
            nsb.append(newValue[i]);
            nsb.append(", ");
        }
        nsb.append(newValue[newValue.length - 1]);

        return "editValue: { recordID: " + recordID + ", old value: " + osb + ", new value: " + nsb + " }";
    }

    private String editStatus(StudentRecord record, String newValue, Manager manager) {
        String oldValue = record.getStatus();
        if (!newValue.equals("active") && !newValue.equals("inactive")) {
            return generateLog("[ERROR]", manager.getManagerId(), "new value " + newValue + " is invalid.");
        }
        return generateLog("[SUCCESS]", manager.getManagerId(), getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private String getEditValueOperationMessage(String recordID, String oldValue, String newValue) {
        return "editValue: { recordID: " + recordID + ", old value: " + oldValue + ", new value: " + newValue + " }";
    }

    private String editStatusDate(StudentRecord record, String newValue, Manager manager) {
        if (!Tool.isDateFormatValid(newValue)) {
            generateLog("[ERROR]", manager.getManagerId(), "the format of new date [" + newValue + "] is invalid.");
        }
        String oldValue = record.getStatusDate();
        record.setStatusDate(newValue);
        return generateLog("[SUCCESS]", manager.getManagerId(), getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private String editAddress(TeacherRecord record, String newValue, Manager manager) {
        String oldValue = record.getAddress();
        record.setAddress(newValue);
        return generateLog("[SUCCESS]", manager.getManagerId(), getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private String editPhone(TeacherRecord record, String newValue, Manager manager) {
        String oldValue = record.getPhone();
        record.setPhone(newValue);
        return generateLog("[SUCCESS]", manager.getManagerId(), getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private String editLocation(TeacherRecord record, String newValue, Manager manager) {
        String oldValue = record.getLocation().toString();
        if (!newValue.equals(Location.LVL) && !newValue.equals(Location.DDO) && !newValue.equals(Location.MTL)) {
            return generateLog("[ERROR]", manager.getManagerId(), "the location [" + newValue +"] is invalid");
        }
        if (newValue.equals(Location.LVL)) {
            record.setLocation(Location.LVL);
        } else if (newValue.equals(Location.MTL)) {
            record.setLocation(Location.MTL);
        } else {
            record.setLocation(Location.DDO);
        }
        return generateLog("[SUCCESS]", manager.getManagerId(), getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    public static void insertRecords(List<Record> records) {
        for (Record record: records) {
            List<Record> recordList = recordMap.get(record.getLastName().charAt(0));
            if (recordList == null) {
                recordList = new LinkedList();
                recordMap.put(record.getLastName().charAt(0), recordList);
            }
            if (record instanceof TeacherRecord) {
                TeacherRecord teacherRecord = new TeacherRecord(generateRecordId("TR"), record.getFirstName(), record.getLastName()
                        , ((TeacherRecord) record).getAddress(), ((TeacherRecord) record).getPhone(), ((TeacherRecord) record).getSpecialization(), location);
                recordList.add(teacherRecord);
                teacherRecordNum++;
            } else if (record instanceof StudentRecord){
                StudentRecord studentRecord = new StudentRecord(generateRecordId("TR"), record.getFirstName(), record.getLastName()
                        , ((StudentRecord) record).getCoursesRegistered(), ((StudentRecord) record).getStatus(), ((StudentRecord) record).getStatusDate());
                recordList.add(studentRecord);
                studentRecordNum++;
            }
        }
    }

    private static void initiate() {
        List<Record> recordList = new LinkedList<>();
        if (location.toString().equals("LVL")) {
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new StudentRecord(generateRecordId("SR"), "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
        } else if (location.toString().equals("MTL")) {
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new StudentRecord(generateRecordId("SR"), "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
            recordList.add(new StudentRecord(generateRecordId("SR"), "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
        } else {
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new StudentRecord(generateRecordId("SR"), "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
        }
        insertRecords(recordList);
        System.out.println("the initial number of records is " + (teacherRecordNum + studentRecordNum));
    }
}
