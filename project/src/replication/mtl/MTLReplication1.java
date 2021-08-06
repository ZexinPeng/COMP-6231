package replication.mtl;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class MTLReplication1 {
    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getMtlHeartbeatPorts()[0]), Configuration.getMtlReplicationPort());
        fbp.start();
    }
}
