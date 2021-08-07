package replication.message;

public class CreateSRecordMessage extends Message {
    public static final String PREFIX = "createSRecord";

    public CreateSRecordMessage(String senderID, String content) {
        super(PREFIX, senderID, content);
    }
}
