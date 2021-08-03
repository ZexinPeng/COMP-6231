package replication.election;

import util.Configuration;

public class Coordinator {
    private String procID;

    public Coordinator(String procID) {
        this.procID = procID;
    }

    public String toString() {
        return "coordinator" + Configuration.getSeparator() + procID;
    }

    public static String getProcIDFromCoordinatorMessage(String message) {
        return message.split(Configuration.getSeparator())[1];
    }
}
