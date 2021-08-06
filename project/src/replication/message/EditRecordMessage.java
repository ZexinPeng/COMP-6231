package replication.message;

import replication.Message;
import util.Configuration;

public class EditRecordMessage extends Message {
    private String recordID;
    private String fieldName;
    private String newValue;
    private String managerID;

    public static final String PREFIX = "editSRecord";

    public EditRecordMessage(String senderID, String content) {
        super(PREFIX, senderID, content);
    }

    public static EditRecordMessage getInstance(String content) {
        String[] msg = content.split(Configuration.getInnerSeparator());
        EditRecordMessage editRecordMessage = new EditRecordMessage(null, null);
        editRecordMessage.setRecordID(msg[0]);
        editRecordMessage.setFieldName(msg[1]);
        editRecordMessage.setNewValue(msg[2]);
        return editRecordMessage;
    }

    public static String getMessageContent(String recordID, String fieldName, String newValue) {
        StringBuilder sb = new StringBuilder();
        sb.append(recordID).append(Configuration.getInnerSeparator()).append(fieldName).append(Configuration.getInnerSeparator())
                .append(newValue);
        return sb.toString();
    }

    public String getRecordID() {
        return recordID;
    }

    public void setRecordID(String recordID) {
        this.recordID = recordID;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(String newValue) {
        this.newValue = newValue;
    }

    public String getManagerID() {
        return managerID;
    }

    public void setManagerID(String managerID) {
        this.managerID = managerID;
    }
}
