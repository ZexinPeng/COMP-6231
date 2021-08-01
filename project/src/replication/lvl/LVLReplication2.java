package replication.lvl;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class LVLReplication2 {
    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getLvlHeartbeatPorts()[1]), Configuration.getLvlReplicationPort());
        fbp.start();
    }
}
