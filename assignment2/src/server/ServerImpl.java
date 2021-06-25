package server;

import ServerApp.Server;
import ServerApp.ServerHelper;
import ServerApp.ServerPOA;
import bean.Location;
import bean.Record;
import bean.StudentRecord;
import bean.TeacherRecord;
import factory.ConfigurationFactory;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import util.Configuration;
import util.Tool;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServerImpl extends ServerPOA {
    private static final HashMap<Character, List<Record>> recordMap = new HashMap<>();
    final private static Configuration configuration = ConfigurationFactory.getConfiguration();
    private static Location location;
    private static int teacherRecordNum = 0;
    private static int studentRecordNum = 0;

    protected static void startServer(String[] args, Location locationPara) {
        if (locationPara == null) {
            Tool.printError("the location of the server should be indicated!");
        }
        location = locationPara;
        startCountThread();
        startTransferRecordThread();
        initiate();
        try {
            ORB orb = ORB.init(args, configuration.getProperties());
            // Portable Object Adapter (POA)
            // get reference to rootpoa & activate the POAManager
            POA rootpoa = (POA) orb.resolve_initial_references("RootPOA");
            rootpoa.the_POAManager().activate();
            // create servant and register it with the ORB
            ServerImpl serverImpl = new ServerImpl();
            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverImpl);
            // and cast the reference to a CORBA reference
            Server href = ServerHelper.narrow(ref);
            // get the root naming context
            // NameService invokes the transient name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt, which is part of the
            // Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            // bind the Object Reference in Naming
            String name = location.toString();
            NameComponent[] path = ncRef.to_name(name);
            ncRef.rebind(path, href);
            System.out.println(location.toString() + " Server is ready.");
            // wait for invocations from clients
            orb.run();
        } catch (WrongPolicy | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound | InvalidName | ServantNotActive | AdapterInactive e) {
            e.printStackTrace();
        }
    }

    @Override
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location, String managerID) {
        synchronized (recordMap) {
            List<Record> teacherRecordList = recordMap.computeIfAbsent(lastName.charAt(0), k -> new LinkedList<>());
            TeacherRecord teacherRecord = new TeacherRecord(generateRecordId("TR"), firstName, lastName, address, phone
                    , specialization, location);
            teacherRecordList.add(teacherRecord);
            teacherRecordNum++;
            return generateLog("[SUCCESS]", managerID, "createTRecord: " + teacherRecord.toString());
        }
    }

    /**
     * This method is responsible to create student record in the current server.
     * @param firstName firstname
     * @param lastName lastname
     * @param courseRegistered all courses should be split by dot, for example "math,english"
     * @param status status
     * @param statusDate statusDate
     * @param managerID managerID
     * @return the log message
     */
    @Override
    public String createSRecord(String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerID) {
        synchronized (recordMap) {
            List<Record> recordList = recordMap.computeIfAbsent(lastName.charAt(0), k -> new LinkedList<>());
            StudentRecord studentRecord = new StudentRecord(generateRecordId("SR"), firstName, lastName, courseRegistered.split(","), status, statusDate);
            recordList.add(studentRecord);
            studentRecordNum++;
            return generateLog("[SUCCESS]", managerID, "createSRecord: " + studentRecord.toString());
        }
    }

    /**
     * Thie method will return the quantity of all records in the server
     * @param managerID managerID
     * @return quantity of records
     */
    @Override
    public String getRecordCounts(String managerID) {
        int[] numArray = getNum();
        return "MTL " + numArray[1] + ", LVL " + numArray[0] + ", DDO " + numArray[2];
    }

    @Override
    public String editRecord(String recordID, String fieldName, String newValue, String managerID) {
        synchronized (recordMap) {
            for (Character key : recordMap.keySet()) {
                List<Record> recordList = recordMap.get(key);
                for (Record record : recordList) {
                    if (record.getRecordID().equals(recordID)) {
                        if (record instanceof StudentRecord) {
                            switch (fieldName) {
                                case "courseRegistered":
                                    return editCourseRegistered((StudentRecord) record, newValue, managerID);
                                case "status":
                                    return editStatus((StudentRecord) record, newValue, managerID);
                                case "statusDate":
                                    return editStatusDate((StudentRecord) record, newValue, managerID);
                                default:
                                    return generateLog("[ERROR]", managerID, " fieldName [" + fieldName + "] is not allowed to modify");
                            }
                        } else if (record instanceof TeacherRecord) {
                            switch (fieldName) {
                                case "address":
                                    return editAddress((TeacherRecord) record, newValue, managerID);
                                case "phone":
                                    return editPhone((TeacherRecord) record, newValue, managerID);
                                case "location":
                                    return editLocation((TeacherRecord) record, newValue, managerID);
                                default:
                                    return generateLog("[ERROR]", managerID, " fieldName [" + fieldName + "] is not allowed to modify");
                            }
                        } else {
                            Tool.printError("wrong type: " + record.getClass().getName());
                        }
                    }
                }
            }
        }
        return generateLog("[ERROR]", managerID, " recordID [" + recordID + "] does not exist.");
    }

    @Override
    public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
        int port;
        switch (remoteCenterServerName) {
            case "MTL":
                port = configuration.getTransferPortMTL();
                break;
            case "DDO":
                port = configuration.getTransferPortDDO();
                break;
            case "LVL":
                port = configuration.getTransferPortLVL();
                break;
            default:
                return generateLog("[ERROR]", managerID, "the location [" + remoteCenterServerName + "] is invalid");
        }
        synchronized (recordMap) {
            for (Character key : recordMap.keySet()) {
                List<Record> recordList = recordMap.get(key);
                for (Record record: recordList) {
                    if (record.getRecordID().equals(recordID)) {
                        try {
                            Socket clientSocket = new Socket(configuration.getHost(), port);
                            DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                            BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                            if (record instanceof StudentRecord) {
                                outToServer.writeBytes( ((StudentRecord)record).toSerialize() + "," + managerID + "\n");
                            }
                            else if (record instanceof TeacherRecord) {
                                outToServer.writeBytes( ((TeacherRecord)record).toSerialize() + "," + managerID + "\n");
                            }
                            else {
                                Tool.printError("wrong type: " + record.getClass().getName());
                            }
                            // get results from the target server
                            String message = inFromServer.readLine();

                            if (message.startsWith("[SUCCESS]")) {
                                recordList.remove(record);
                            }
                            clientSocket.close();
                            return generateLog(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return generateLog("[ERROR]", managerID, " recordID [" + recordID + "] does not exist.");
    }

    /**
     * write the content into the log file
     * @param status "[SUCCESS]" or "[ERROR]"
     * @param managerID manageID
     * @param operationMessage the massage of the current operation
     * @return the generated log message
     */
    private static String generateLog(String status, String managerID, String operationMessage) {
        String message;
        if (status.equals("[ERROR]")) {
            message = status + " date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    + ", managerID: " + managerID + ", error message: " + operationMessage;
        }
        else {
            message = status + " date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    + ", managerID: " + managerID + ", operation: " + operationMessage;
        }
        System.out.println(message);
        Tool.write2LogFile(message, configuration.getServerLogDirectory(), location.toString());
        return message;
    }

    private String generateLog(String message) {
        System.out.println(message);
        Tool.write2LogFile(message, configuration.getServerLogDirectory(), location.toString());
        return message;
    }

    /**
     *
     * @param prefiex "TR" or "SR"
     * @return id
     */
    private static String generateRecordId(String prefiex) {
        int port;
        if (prefiex.equals("TR")) {
            port = configuration.getTeacherPortID();
        }
        else {
            port = configuration.getStudentPortID();
        }
        int id = getNum(port);
        StringBuilder prefiexBuilder = new StringBuilder(prefiex);
        for (int i = 0; i < 5 - String.valueOf(id).length(); i++) {
            prefiexBuilder.append("0");
        }
        prefiex = prefiexBuilder.toString();
        return prefiex + id;
    }

    /**
     * This method will return the unique id in the system according to the parameter port.
     * @param port is subject to the Record type
     * @return the quantity of the certain Record type in the system.
     */
    private static int getNum(int port) {
        try (DatagramSocket aSocket = new DatagramSocket()) {
            byte[] m = new byte[4];
            InetAddress aHost = InetAddress.getByName(configuration.getHost());
            DatagramPacket request =
                    new DatagramPacket(m, 1, aHost, port);
            aSocket.send(request);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            return Tool.bytes2Int(reply.getData());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return -1;
        }
    }

    /**
     * This method will return the total number of records in all servers
     * @return the format of the result array is [numLVL, numMTL, numDDO]
     */
    private static synchronized int[] getNum() {
        int numLVL = 0, numMTL = 0, numDDO = 0;
        try (DatagramSocket aSocket = new DatagramSocket()) {
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
            for (int value : portArr) {
                if (value == ignoredPort) {
                    continue;
                }
                DatagramPacket request =
                        new DatagramPacket(m, 1, aHost, value);
                aSocket.send(request);
                byte[] buffer = new byte[1000];
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(reply);
                if (value == configuration.getPortLVL()) {
                    numLVL = Tool.bytes2Int(reply.getData());
                } else if (value == configuration.getPortDDO()) {
                    numDDO = Tool.bytes2Int(reply.getData());
                } else {
                    numMTL = Tool.bytes2Int(reply.getData());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new int[]{numLVL, numMTL, numDDO};
    }

    /**
     * start a new thread to listen
     */
    private static void startCountThread() {
        new Thread(() -> {
            int port;
            if (location.equals(Location.LVL)) {
                port = configuration.getPortLVL();
            } else if (location.equals(Location.DDO)) {
                port = configuration.getPortDDO();
            } else {
                port = configuration.getPortMTL();
            }
            try (DatagramSocket aSocket = new DatagramSocket(port)) {
                // create socket at agreed port
                byte[] buffer = new byte[1000];
                while (true) {
                    System.out.println("Record Count Thread is ready.");
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request);
                    DatagramPacket reply = new DatagramPacket(Tool.int2ByteArray(teacherRecordNum + studentRecordNum), 4,
                            request.getAddress(), request.getPort());
                    aSocket.send(reply);
                }
            } catch (SocketException e) {
                System.out.println("Socket: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO: " + e.getMessage());
            }
        }).start();
    }

    /**
     * get record from another server and store it locally using tcp
     */
    private static void startTransferRecordThread() {
        new Thread(() -> {
            int port;
            switch (location.toString()) {
                case "MTL":
                    port = configuration.getTransferPortMTL();
                    break;
                case "DDO":
                    port = configuration.getTransferPortDDO();
                    break;
                case "LVL":
                    port = configuration.getTransferPortLVL();
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + location.toString());
            }
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Transfer Record Thread is ready.");
                while (true) {
                    Socket connectionSocket = serverSocket.accept();
                    //Get values from client
                    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                    //Get OutputStream at server to send values to client
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
                    //Get the input message from cli.ent and then print
                    String message = inFromClient.readLine();
                    Record record;
                    if (message.startsWith("student")) {
                        record = StudentRecord.deserialize(message);
                    } else {
                        record = TeacherRecord.deserialize(message);
                    }
                    if (record == null) {
                        outToClient.writeBytes(generateLog("[ERROR]", extractManagerID(message) , " cannot deserialize " + message));
                        continue;
                    }
                    synchronized (recordMap) {
                        List<Record> recordList = new LinkedList<>();
                        recordList.add(record);
                        insertRecords(recordList);
                    }
                    outToClient.writeBytes(generateLog("[SUCCESS]", extractManagerID(message) , " create record " + record.toString()) + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * insert some records at the beginning
     */
    private static void initiate() {
        List<Record> recordList = new LinkedList<>();
        if (location.toString().equals("LVL")) {
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString()));
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString()));
            recordList.add(new StudentRecord(generateRecordId("SR"), "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
        } else if (location.toString().equals("MTL")) {
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString()));
            recordList.add(new StudentRecord(generateRecordId("SR"), "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
        } else {
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString()));
            recordList.add(new StudentRecord(generateRecordId("SR"), "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString()));
            recordList.add(new TeacherRecord(generateRecordId("TR"), "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString()));
        }
        insertRecords(recordList);
        System.out.println("the initial number of records is " + (teacherRecordNum + studentRecordNum));
    }

    /**
     * insert recordsList into the server
     * @param records recordList
     */
    private static void insertRecords(List<Record> records) {
        for (Record record: records) {
            List<Record> recordList = recordMap.computeIfAbsent(record.getLastName().charAt(0), k -> new LinkedList<>());
            if (record instanceof TeacherRecord) {
                TeacherRecord teacherRecord = new TeacherRecord(record.getRecordID(), record.getFirstName(), record.getLastName()
                        , ((TeacherRecord) record).getAddress(), ((TeacherRecord) record).getPhone(), ((TeacherRecord) record).getSpecialization(), location.toString());
                recordList.add(teacherRecord);
                teacherRecordNum++;
            } else if (record instanceof StudentRecord){
                StudentRecord studentRecord = new StudentRecord(record.getRecordID(), record.getFirstName(), record.getLastName()
                        , ((StudentRecord) record).getCoursesRegistered(), ((StudentRecord) record).getStatus(), ((StudentRecord) record).getStatusDate());
                recordList.add(studentRecord);
                studentRecordNum++;
            }
        }
    }

    private String editCourseRegistered(StudentRecord studentRecord, String newValue, String managerID) {
        String[] oldValue = studentRecord.getCoursesRegistered();
        studentRecord.setCoursesRegistered(newValue.split(","));
        return generateLog("[SUCCESS]", managerID, generateCourseRegisteredMessage(studentRecord.getRecordID(), oldValue, newValue.split(",")));
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

    private String editStatus(StudentRecord record, String newValue, String managerID) {
        String oldValue = record.getStatus();
        if (!newValue.equals("active") && !newValue.equals("inactive")) {
            return generateLog("[ERROR]", managerID, "new value [" + newValue + "] in filed [status] is invalid.");
        }
        return generateLog("[SUCCESS]", managerID, getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private String getEditValueOperationMessage(String recordID, String oldValue, String newValue) {
        return "editValue: { recordID: " + recordID + ", old value: " + oldValue + ", new value: " + newValue + " }";
    }

    private String editStatusDate(StudentRecord record, String newValue, String managerID) {
        if (!Tool.isDateFormatValid(newValue)) {
            return generateLog("[ERROR]", managerID, "the format of new date [" + newValue + "] is invalid.");
        }
        String oldValue = record.getStatusDate();
        record.setStatusDate(newValue);
        return generateLog("[SUCCESS]", managerID, getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private String editAddress(TeacherRecord record, String newValue, String managerID) {
        String oldValue = record.getAddress();
        record.setAddress(newValue);
        return generateLog("[SUCCESS]", managerID, getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private String editPhone(TeacherRecord record, String newValue, String managerID) {
        String oldValue = record.getPhone();
        record.setPhone(newValue);
        return generateLog("[SUCCESS]", managerID, getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private String editLocation(TeacherRecord record, String newValue, String managerID) {
        String oldValue = record.getLocation();
        if (!newValue.equals(Location.LVL.toString()) && !newValue.equals(Location.DDO.toString()) && !newValue.equals(Location.MTL.toString())) {
            return generateLog("[ERROR]", managerID, "the new value [" + newValue +"] in filed [location] is invalid");
        }
        if (newValue.equals(Location.LVL)) {
            record.setLocation(Location.LVL.toString());
        } else if (newValue.equals(Location.MTL)) {
            record.setLocation(Location.MTL.toString());
        } else {
            record.setLocation(Location.DDO.toString());
        }
        return generateLog("[SUCCESS]", managerID, getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
    }

    private static String extractManagerID(String message) {
        String[] arr = message.split(",");
        return arr[arr.length - 1];
    }
}
