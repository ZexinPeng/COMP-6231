package replication.message;

import replication.Message;

public class RecordCountsMessage extends Message {
    public static final String PREFIX = "recordCounts";

    public RecordCountsMessage(String senderID, String content) {
        super(PREFIX, senderID, content);
    }
}
