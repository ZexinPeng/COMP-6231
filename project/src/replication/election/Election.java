package replication.election;

import util.Configuration;

public class Election {
    private String procID;

    private int destination;

    public Election(String procID, int destination) {
        this.destination = destination;
        this.procID = procID;
    }

    public String toString() {
        return "election" + Configuration.getSeparator() + procID + Configuration.getSeparator();
    }

    public int getDestination() {
        return destination;
    }
}
