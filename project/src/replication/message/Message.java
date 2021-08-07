package replication.message;

import util.Configuration;

/**
 * normal message with format type + Configuration.getSeparator() + procID + Configuration.getSeparator() + content + Configuration.getSeparator()
 */
public class Message {
    protected String senderID;
    protected String content;
    protected String type;

    public Message(String type, String senderID, String content) {
        this.senderID = senderID;
        this.content = content;
        this.type = type;
    }

    public Message(String rawMessage) {
        String[] msg = rawMessage.split(Configuration.getSeparator());
        this.type = msg[0];
        this.senderID = msg[1];
        if (msg.length <= 2) {
            return;
        }
        content = msg[2];
    }

    public String toString() {
        return type + Configuration.getSeparator() + senderID + Configuration.getSeparator() + content + Configuration.getSeparator();
    }

    public String getSenderID() {
        return senderID;
    }

    public String getContent() {
        return content;
    }

    public String getType() {
        return type;
    }
}
