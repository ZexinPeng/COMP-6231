package replication.lvl;

import replication.FifoBroadcastProcess;
import util.Configuration;

public class LVLReplication1 {

    public static void main(String[] args) {
        FifoBroadcastProcess fbp = new FifoBroadcastProcess( String.valueOf(Configuration.getLvlHeartbeatPorts()[0]), Configuration.getLvlReplicationPort());
        fbp.start();
    }

}
