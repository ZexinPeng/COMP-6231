package replication.message;

import replication.Message;

public class HeaderMessage extends Message {
    public static final String PREFIX = "header";

    public HeaderMessage(String senderID, String content) {
        super(PREFIX, senderID, content);
    }
}
