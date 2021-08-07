package replication.message;

public class CreateTRecordMessage extends Message {
    public static final String PREFIX = "createTRecord";

    public CreateTRecordMessage(String senderID, String content) {
        super(PREFIX, senderID, content);
    }
}
