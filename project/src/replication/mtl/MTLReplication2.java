package replication.mtl;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class MTLReplication2 {
    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getMtlHeartbeatPorts()[1]), Configuration.getMtlReplicationPort());
        fbp.start();
    }
}
