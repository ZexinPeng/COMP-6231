package replication;

import bean.Record;
import bean.StudentRecord;
import bean.TeacherRecord;
import replication.message.RecordCountsMessage;
import util.Configuration;
import util.Tool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ReplicationImpl implements Replication{
    private static final HashMap<Character, List<Record>> recordMap = new HashMap<>();
    private static int teacherRecordNum = 0;
    private static int studentRecordNum = 0;

    //String that uniquely identifies the process
    public String procID;

    public String createTRecord(TeacherRecord teacherRecord, String managerID) {
        synchronized (recordMap) {
            List<Record> teacherRecordList = recordMap.computeIfAbsent(teacherRecord.getLastName().charAt(0), k -> new LinkedList<>());
            teacherRecordList.add(teacherRecord);
            teacherRecordNum++;
            return generateLog("[SUCCESS]", managerID, "createTRecord: " + teacherRecord);
        }
    }

    public String createSRecord(StudentRecord studentRecord, String managerID) {
        synchronized (recordMap) {
            List<Record> recordList = recordMap.computeIfAbsent(studentRecord.getLastName().charAt(0), k -> new LinkedList<>());
            recordList.add(studentRecord);
            studentRecordNum++;
            return generateLog("[SUCCESS]", managerID, "createSRecord: " + studentRecord);
        }
    }

    @Override
    public String createTRecord(String message) {
        return createTRecord(TeacherRecord.deserialize(message), Configuration.getManagerId());
    }

    @Override
    public String createSRecord(String message) {
        return createSRecord(StudentRecord.deserialize(message), Configuration.getManagerId());
    }

    @Override
    public String getRecordCounts() {
        return new RecordCountsMessage(procID, String.valueOf(studentRecordNum + teacherRecordNum)).toString();
    }

    @Override
    public String editRecord(String recordID, String fieldName, String newValue, String managerID) {
        return null;
    }

    @Override
    public String removeRecord(String managerID, String recordID, String remoteCenterServerName) {
        return null;
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
            aSocket.setSoTimeout(1000);
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
            System.out.println("Connection with ID Server timeout! Please check and start ID Server.");
            return -1;
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
        Tool.write2LogFile(message, Configuration.getReplicationLogDirectory(), this.procID);
        return message;
    }
}
