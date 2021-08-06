package replication.mtl;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class MTLReplication3 {
    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getMtlHeartbeatPorts()[2]), Configuration.getMtlReplicationPort());
        fbp.start();
    }
}
