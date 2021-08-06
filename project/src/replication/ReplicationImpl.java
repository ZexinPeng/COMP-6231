package replication;

import bean.Location;
import bean.Record;
import bean.StudentRecord;
import bean.TeacherRecord;
import replication.message.EditRecordMessage;
import replication.message.RecordCountsMessage;
import util.Configuration;
import util.Tool;

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
    public String editRecord(String messageContent) {
        return editRecord(messageContent, Configuration.getManagerId());
    }

    @Override
    public String removeRecord(String messageContent) {
        String recordID = messageContent;
        synchronized (recordMap) {
            for (Character key : recordMap.keySet()) {
                List<Record> recordList = recordMap.get(key);
                for (Record record : recordList) {
                    if (record.getRecordID().equals(recordID)) {
                        recordList.remove(record);
                        generateLog("[SUCCESS]", Configuration.getManagerId(), "remove record: " + recordID);
                        if (record instanceof TeacherRecord) {
                            teacherRecordNum--;
                            return ((TeacherRecord) record).toSerialize();
                        }
                        else if (record instanceof StudentRecord) {
                            studentRecordNum--;
                            return ((StudentRecord) record).toSerialize();
                        }
                    }
                }
            }
        }
        return generateLog("[ERROR]", Configuration.getManagerId(), "no such record [" + recordID +"]");
    }

    @Override
    public String insertRecord(String messageContent) {
        Record record;
        if (messageContent.startsWith("student")) {
            record = StudentRecord.deserialize(messageContent);
        }
        else {
            record = TeacherRecord.deserialize(messageContent);
        }
        synchronized (recordMap) {
            List<Record> recordList = recordMap.computeIfAbsent(record.getLastName().charAt(0), k -> new LinkedList<>());
            if (record instanceof TeacherRecord) {
                TeacherRecord teacherRecord = new TeacherRecord(record.getRecordID(), record.getFirstName(), record.getLastName()
                        , ((TeacherRecord) record).getAddress(), ((TeacherRecord) record).getPhone(), ((TeacherRecord) record).getSpecialization(), getLocation());
                recordList.add(teacherRecord);
                teacherRecordNum++;
            } else if (record instanceof StudentRecord){
                StudentRecord studentRecord = new StudentRecord(record.getRecordID(), record.getFirstName(), record.getLastName()
                        , ((StudentRecord) record).getCoursesRegistered(), ((StudentRecord) record).getStatus(), ((StudentRecord) record).getStatusDate());
                recordList.add(studentRecord);
                studentRecordNum++;
            }
        }
        return generateLog("[SUCCESS]", Configuration.getManagerId(), "successfully inserting record " + record);
    }

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

    public String editRecord(String messageContent, String managerID) {
        EditRecordMessage editRecordMessage = EditRecordMessage.getInstance(messageContent);
        String recordID = editRecordMessage.getRecordID();
        String fieldName = editRecordMessage.getFieldName();
        String newValue = editRecordMessage.getNewValue();
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

    private String getEditValueOperationMessage(String recordID, String oldValue, String newValue) {
        return "editValue: { recordID: " + recordID + ", old value: " + oldValue + ", new value: " + newValue + " }";
    }

    private String editLocation(TeacherRecord record, String newValue, String managerID) {
        String oldValue = record.getLocation();
        if (!newValue.equals(Location.LVL.toString()) && !newValue.equals(Location.DDO.toString()) && !newValue.equals(Location.MTL.toString())) {
            return generateLog("[ERROR]", managerID, "the new value [" + newValue +"] in filed [location] is invalid");
        }
        if (newValue.equals(Location.LVL.toString())) {
            record.setLocation(Location.LVL.toString());
        } else if (newValue.equals(Location.MTL.toString())) {
            record.setLocation(Location.MTL.toString());
        } else {
            record.setLocation(Location.DDO.toString());
        }
        return generateLog("[SUCCESS]", managerID, getEditValueOperationMessage(record.getRecordID(), oldValue, newValue));
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

    private String getLocation() {
        int procID = Integer.parseInt(this.procID);
        int[] ports = Configuration.getLvlHeartbeatPorts();
        for (int port: ports) {
            if (port == procID) {
                return Location.LVL.toString();
            }
        }
        ports = Configuration.getMtlHeartbeatPorts();
        for (int port: ports) {
            if (port == procID) {
                return Location.MTL.toString();
            }
        }
        return Location.DDO.toString();
    }
}
