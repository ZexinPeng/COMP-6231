package replication.heartbeat;

import replication.FifoBroadcastProcess;
import util.Configuration;
import util.Tool;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replications send heartbeat messages to other replications in the group.
 */
public class HeartbeatListenerThread extends Thread{

    private final FifoBroadcastProcess rbp;

    private final int procID;

    // other replications' ports list
    private final List<Integer> alivePortList = new LinkedList<>();

    // this map can store the last heartbeat message from other replications <procID, Timestamp>
    private final Map<Integer, Long> heartbeatUpdateTimestampMap = new ConcurrentHashMap<>();

    public HeartbeatListenerThread(FifoBroadcastProcess rbp) {
        this.rbp = rbp;
        procID = Integer.parseInt(rbp.procID);
        // other replications' ports array
        int[] portArr = getHeartbeatPortArrayByProcID(procID);
        if (portArr == null) {
            System.out.println("cannot find heartbeat port: " + rbp.procID);
            return;
        }
        for (int j : portArr) {
            if (j == procID) {
                continue;
            }
            alivePortList.add(j);
            heartbeatUpdateTimestampMap.put(j, System.currentTimeMillis());
        }
    }

    /**
     * start a new thread that can send heartbeat messages to other replications every 2s
     * this method also can reply other
     */
    public void run()
    {
        // start the thread that will send heartbeat messages to alive replications
        startSendThread();
        // start the thread that checks the timeout replication in the group
        startMonitorThread();
    }

    public void updateTimestamp(int procID) {
        heartbeatUpdateTimestampMap.put(procID, System.currentTimeMillis());
        synchronized (alivePortList) {
            if (!alivePortList.contains(procID)) {
                System.out.println("reconnect with process: " + procID);
                alivePortList.add(procID);
            }
        }
    }

    private void startMonitorThread() {
        new Thread(()-> {
            while (true) {
                Long currentTime = System.currentTimeMillis();
                for (Integer procID: heartbeatUpdateTimestampMap.keySet()) {
                    if (currentTime - heartbeatUpdateTimestampMap.get(procID) > Configuration.getHeartbeatPeriod() * 2L) {
                        processTimeout(procID);
                    }
                }
                try {
                    Thread.sleep(Configuration.getHeartbeatPeriod() * 2L);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void allReplicationsHaveFailed() {
        synchronized (alivePortList) {
            for(Integer procID: alivePortList) {
                processTimeout(procID);
            }
        }
    }

    /**
     * deal with the timeout replication
     * @param procID the processID
     */
    private void processTimeout(int procID) {
        System.out.println("process: " + procID + " timeout!");
        heartbeatUpdateTimestampMap.remove(procID);
        synchronized (alivePortList) {
            alivePortList.remove(new Integer(procID));
        }
        if (String.valueOf(procID).equals(rbp.getHeader())) {
            rbp.getElectionThread().startElection();
        }
        // TODO restart the failed replication
    }

    /**
     * This thread can send heartbeat messages in portList periodically.
     */
    private void startSendThread() {
        new Thread(()-> {
            System.out.println("Heartbeat listener for process [" + rbp.procID +"] starts successfully");
            while (true) {
                synchronized (alivePortList) {
                    for (Integer port: alivePortList) {
                        Tool.sendMessage(new HeartbeatMessage(String.valueOf(procID)).toString(), Configuration.getHost(), port);
                    }
                }
                try {
                    Thread.sleep(Configuration.getHeartbeatPeriod());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected static int[] getHeartbeatPortArrayByProcID(int procID) {
        int[] portArr = Configuration.getLvlHeartbeatPorts();
        for (int value : portArr) {
            if (procID == value) {
                return portArr;
            }
        }
        portArr = Configuration.getDdoHeartbeatPorts();
        for (int k : portArr) {
            if (procID == k) {
                return portArr;
            }
        }
        portArr = Configuration.getMtlHeartbeatPorts();
        for (int j : portArr) {
            if (procID == j) {
                return portArr;
            }
        }
        return null;
    }

    public List<Integer> getAlivePortList() {
        return alivePortList;
    }
}
