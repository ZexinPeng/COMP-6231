package replication.lvl;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class LVLReplication3 {
    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getLvlHeartbeatPorts()[2]), Configuration.getLvlReplicationPort());
        fbp.start();
    }
}
