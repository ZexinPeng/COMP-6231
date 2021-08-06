package replication.message;

import replication.Message;

public class RemoveRecordMessage extends Message {
    public static final String PREFIX = "removeRecordMessage";

    public RemoveRecordMessage(String senderID, String content) {
        super(PREFIX, senderID, content);
    }
}
