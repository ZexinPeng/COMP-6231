package replication.message;

import replication.Message;

public class InsertRecordMessage extends Message {
    public static final String PREFIX = "insertRecordMessage";

    public InsertRecordMessage(String senderID, String content) {
        super(PREFIX, senderID, content);
    }
}
