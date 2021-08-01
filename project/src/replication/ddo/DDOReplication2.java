package replication.ddo;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class DDOReplication2 {
    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getDdoHeartbeatPorts()[1]), Configuration.getDdoReplicationPort());
        fbp.start();
    }
}
