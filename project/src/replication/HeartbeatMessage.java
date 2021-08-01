package replication;

import util.Configuration;

public class HeartbeatMessage {
    private String procID;

    public HeartbeatMessage(String procID) {
        this.procID = procID;
    }

    public String toString() {
        return "heartbeat" + Configuration.getSeparator() + procID;
    }
}
