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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServerImpl extends ServerPOA {
    private static HashMap<Character, List<Record>> recordMap = new HashMap<>();
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
        } catch (WrongPolicy | CannotProceed | org.omg.CosNaming.NamingContextPackage.InvalidName | NotFound | InvalidName | ServantNotActive | AdapterInactive wrongPolicy) {
            wrongPolicy.printStackTrace();
        }
    }

    @Override
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location, String managerID) {
        List<Record> teacherRecordList = recordMap.computeIfAbsent(lastName.charAt(0), k -> new LinkedList<>());
        TeacherRecord teacherRecord = new TeacherRecord(generateRecordId("TR"), firstName, lastName, address, phone
                , specialization, location);
        teacherRecordList.add(teacherRecord);
        teacherRecordNum++;
        return generateLog("[SUCCESS]", managerID, "createTRecord: " + teacherRecord.toString());
    }

    /**
     * This method is responsible to create student record in the current server.
     * @param firstName firstname
     * @param lastName lastname
     * @param courseRegistered all courses should be split by dot, for example "math,english"
     * @param status
     * @param statusDate
     * @param managerID
     * @return the log message
     */
    @Override
    public String createSRecord(String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerID) {
        List<Record> recordList = recordMap.get(lastName.charAt(0));
        if (recordList == null) {
            recordList = new LinkedList();
            recordMap.put(lastName.charAt(0), recordList);
        }
        StudentRecord studentRecord = new StudentRecord(generateRecordId("SR"), firstName, lastName, courseRegistered.split(","), status, statusDate);
        recordList.add(studentRecord);
        studentRecordNum++;
        return generateLog("[SUCCESS]", managerID, "createSRecord: " + studentRecord.toString());
    }

    @Override
    public String getRecordCounts(String managerID) {
        int[] numArray = getNum();
        return "MTL " + numArray[1] + ", LVL " + numArray[0] + ", DDO " + numArray[2];
    }

    @Override
    public String editRecord(String recordID, String fieldName, String newValue, String managerID) {
        return "123";
    }

    @Override
    public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
        return "123";
    }

    /**
     * write the content into the log file
     * @param status "[SUCCESS]" or "[ERROR]"
     * @param managerID manageID
     * @param operationMessage the massage of the current operation
     * @return the generated log message
     */
    private String generateLog(String status, String managerID, String operationMessage) {
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
}
