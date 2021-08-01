package replication.ddo;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class DDOReplication1 {
    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getDdoHeartbeatPorts()[0]), Configuration.getDdoReplicationPort());
        fbp.start();
    }
}
