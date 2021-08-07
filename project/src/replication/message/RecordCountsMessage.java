package replication.message;

public class RecordCountsMessage extends Message {
    public static final String PREFIX = "recordCounts";

    public RecordCountsMessage(String senderID, String content) {
        super(PREFIX, senderID, content);
    }

    public static String extractReply(String receivedMessage) {
        return new Message(receivedMessage).getContent();
    }
}
