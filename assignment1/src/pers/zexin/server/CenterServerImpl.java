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
    HashMap<Character, List<Record>> recordMap = new HashMap<>();
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
                    numDDO = configuration.getPortDDO();
                } else {
                    numMTL = configuration.getPortMTL();
                }
            }
        }catch (SocketException e){System.out.println(e);
        }catch (IOException e){System.out.println(e);
        }finally {if(aSocket != null) aSocket.close();}
        return "MTL " + numMTL + ", LVL " + numLVL + ", DDO " + numDDO;
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
        String message;
        if (status.equals("[ERROR]")) {
            message = status + " something goes wrong in the server.";
        }
        else {
            message = status + " date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    + ", managerID: " + managerID + ", operation: " + operationaMessage;
        }
        System.out.println(message);
        Tool.write2LogFile(message, configuration.getServerLogDirectory(), location.toString());
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
}
