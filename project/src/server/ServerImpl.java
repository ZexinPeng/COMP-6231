package server;

import ServerApp.Server;
import ServerApp.ServerHelper;
import ServerApp.ServerPOA;
import bean.Location;
import bean.StudentRecord;
import bean.TeacherRecord;
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
import replication.message.*;
import util.Configuration;
import util.Tool;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServerImpl extends ServerPOA {
    // lvl header, ddo header, mtl header
    protected int[] headerPorts = new int[]{-1, -1, -1};
    protected Location location;

    protected void startServer(String[] args, Location locationPara) {
        if (locationPara == null) {
            Tool.printError("the location of the server should be indicated!");
        }
        location = locationPara;
        startRouter();
        getHeaderFromReplications();
        initiate();
        try {
            Properties properties = new Properties();
            properties.setProperty("org.omg.CORBA.ORBInitialPort", "1050");
            ORB orb = ORB.init(args, properties);
            // Portable Object Adapter (POA)
            // get reference to rootpoa & activate the POAManager
            POA rootpoa = (POA) orb.resolve_initial_references("RootPOA");
            rootpoa.the_POAManager().activate();
            // create servant and register it with the ORB
            ServerImpl serverImpl = this;
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
        TeacherRecord teacherRecord = new TeacherRecord(generateRecordId("TR"), firstName, lastName, address, phone
                    , specialization, location);
        while (getCurrentReplicationGroupHeader() == -1) {
            getHeaderFromReplications();
        }
        String reply = Tool.sendMessageWithReply(new CreateTRecordMessage(location, teacherRecord.toSerialize()).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
        while (reply == null) {
            getHeaderFromReplications();
            reply = Tool.sendMessageWithReply(new CreateTRecordMessage(location, teacherRecord.toSerialize()).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
        }
        System.out.println(reply);
        return reply;
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
        StudentRecord studentRecord = new StudentRecord(generateRecordId("SR"), firstName, lastName, StudentRecord.convertCoursesRegistered2Arr(courseRegistered)
                , status, statusDate);
        while (getCurrentReplicationGroupHeader() == -1) {
            getHeaderFromReplications();
        }
        String reply = Tool.sendMessageWithReply(new CreateSRecordMessage(location.toString(), studentRecord.toSerialize()).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
        while (reply == null) {
            getHeaderFromReplications();
            reply = Tool.sendMessageWithReply(new CreateSRecordMessage(location.toString(), studentRecord.toSerialize()).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
        }
        System.out.println(reply);
        return reply;
    }

    /**
     * Thie method will return the quantity of all records in the server
     * @param managerID managerID
     * @return quantity of records
     */
    @Override
    public String getRecordCounts(String managerID) {
        String[] msgArr = new String[headerPorts.length];
        for (int i = 0; i < headerPorts.length; i++) {
            while (headerPorts[i] == -1) {
                getHeaderFromReplications();
            }
            String reply = Tool.sendMessageWithReply(new RecordCountsMessage(location.toString(), null).toString(), Configuration.getHost(), headerPorts[i]);
            while (reply == null) {
                getHeaderFromReplications();
                reply = Tool.sendMessageWithReply(new RecordCountsMessage(location.toString(), null).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
            }
            msgArr[i] = reply;
        }
        String str = "lvl: " + RecordCountsMessage.extractReply(msgArr[0]) + ", ddo: " + RecordCountsMessage.extractReply(msgArr[1]) + ", mtl: " + RecordCountsMessage.extractReply(msgArr[2]);
        System.out.println(str);
        return str;
    }

    @Override
    public String editRecord(String recordID, String fieldName, String newValue, String managerID) {
        while (getCurrentReplicationGroupHeader() == -1) {
            getHeaderFromReplications();
        }
        String messageContent = EditRecordMessage.getMessageContent(recordID, fieldName, newValue);
        String reply = Tool.sendMessageWithReply(new EditRecordMessage(location.toString(), messageContent).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
        while (reply == null) {
            getHeaderFromReplications();
            reply = Tool.sendMessageWithReply(new EditRecordMessage(location.toString(), messageContent).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
        }
        System.out.println(reply);
        return reply;
    }

    @Override
    public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
        int port;
        switch (remoteCenterServerName) {
            case "MTL":
                port = headerPorts[getHeaderIndex(Location.MTL)];
                break;
            case "DDO":
                port = headerPorts[getHeaderIndex(Location.DDO)];
                break;
            case "LVL":
                port = headerPorts[getHeaderIndex(Location.LVL)];
                break;
            default:
                return generateLog("[ERROR]", managerID, "the location [" + remoteCenterServerName + "] is invalid");
        }

        while (getCurrentReplicationGroupHeader() == -1) {
            getHeaderFromReplications();
        }
        String reply = Tool.sendMessageWithReply(new RemoveRecordMessage(location.toString(), recordID).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
        while (reply == null) {
            getHeaderFromReplications();
            reply = Tool.sendMessageWithReply(new RemoveRecordMessage(location.toString(), recordID).toString(), Configuration.getHost(), getCurrentReplicationGroupHeader());
        }
        if (reply.startsWith("[ERROR]")) {
            return generateLog("[ERROR]", managerID, reply);
        }
        String recordMsg = reply;

        reply = Tool.sendMessageWithReply(new InsertRecordMessage(location.toString(), recordMsg).toString(), Configuration.getHost(), port);
        while (reply == null) {
            getHeaderFromReplications();
            reply = Tool.sendMessageWithReply(new InsertRecordMessage(location.toString(), recordMsg).toString(), Configuration.getHost(), port);
        }
        if (reply.startsWith("[SUCCESS]")) {
            return generateLog("[SUCCESS]", managerID, "successfully transferring recordID [" + recordID + "] from " + location + " to " +  remoteCenterServerName);
        }
        return generateLog("[ERROR]", managerID, reply);
    }

    private void getHeaderFromReplications() {
        int[] requestPorts = Configuration.getLvlHeartbeatPorts();
        for (int requestPort : requestPorts) {
            Tool.sendMessage(new HeaderMessage(location.toString(), null).toString(), Configuration.getHost(), requestPort);
        }
        requestPorts = Configuration.getDdoHeartbeatPorts();
        for (int requestPort : requestPorts) {
            Tool.sendMessage(new HeaderMessage(location.toString(), null).toString(), Configuration.getHost(), requestPort);
        }
        requestPorts = Configuration.getMtlHeartbeatPorts();
        for (int requestPort : requestPorts) {
            Tool.sendMessage(new HeaderMessage(location.toString(), null).toString(), Configuration.getHost(), requestPort);
        }
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
        Tool.write2LogFile(message, Configuration.getServerLogDirectory(), location.toString());
        return message;
    }

    private String generateLog(String message) {
        System.out.println(message);
        Tool.write2LogFile(message, Configuration.getServerLogDirectory(), location.toString());
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
            port = Configuration.getTeacherIdPort();
        }
        else {
            port = Configuration.getStudentIdPort();
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
            InetAddress aHost = InetAddress.getByName(Configuration.getHost());
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
     * insert some records at the beginning
     */
    private void initiate() {
        if (location.toString().equals("LVL")) {
            createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString(), Configuration.getManagerId());
            createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString(), Configuration.getManagerId());
            createSRecord("mockFirstName", "mockLastName", StudentRecord.convertCoursesRegistered2Serialize(new String[]{"mockCourse1", "mockCourse2"}), "active", Tool.getCurrentTime(), Configuration.getManagerId());
        } else if (location.toString().equals("MTL")) {
            createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString(), Configuration.getManagerId());
            createSRecord("mockFirstName", "mockLastName", StudentRecord.convertCoursesRegistered2Serialize(new String[]{"mockCourse1", "mockCourse2"}), "active", Tool.getCurrentTime(), Configuration.getManagerId());
        } else {
            createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString(), Configuration.getManagerId());
            createSRecord("mockFirstName", "mockLastName", StudentRecord.convertCoursesRegistered2Serialize(new String[]{"mockCourse1", "mockCourse2"}), "active", Tool.getCurrentTime(), Configuration.getManagerId());
            createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString(), Configuration.getManagerId());
            createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location.toString(), Configuration.getManagerId());
        }
    }

    private void startRouter() {
        Router router = new Router(this);
        router.startRouter();
    }

    private int getCurrentReplicationGroupHeader() {
        if (location.equals(Location.LVL)) {
            return headerPorts[0];
        } else if (location.equals(Location.DDO)) {
            return headerPorts[1];
        } else {
            return headerPorts[2];
        }
    }

    protected int getHeaderIndex(Location location) {
        if (location.equals(Location.LVL)) {
            return 0;
        } else if (location.equals(Location.DDO)) {
            return 1;
        } else {
            return 2;
        }
    }
}
