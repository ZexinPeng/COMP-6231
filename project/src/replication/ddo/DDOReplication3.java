package replication.ddo;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class DDOReplication3 {
    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getDdoHeartbeatPorts()[2]), Configuration.getDdoReplicationPort());
        fbp.start();
    }
}
