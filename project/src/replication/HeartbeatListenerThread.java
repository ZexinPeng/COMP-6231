package replication;

import util.Configuration;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Replications send heartbeat messages to other replications in the group.
 */
public class HeartbeatListenerThread extends Thread{

    private FifoBroadcastProcess rbp;

    private int procID;

    // other replications' ports list
    private List<Integer> alivePortList = new Vector<>();

    // other replications' ports array
    private int[] portArr;

    // this map can store the last heartbeat message from other replications <procID, Timestamp>
    private Map<Integer, Long> heartbeatUpdateTimestampMap = new ConcurrentHashMap<>();

    public HeartbeatListenerThread(FifoBroadcastProcess rbp) {
        this.rbp = rbp;
        procID = Integer.parseInt(rbp.procID);
        portArr = getHeartbeatPortArrayByProcID(procID);
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

    protected void updateTimestamp(int procID) {
        heartbeatUpdateTimestampMap.put(procID, System.currentTimeMillis());
        if (!alivePortList.contains(procID)) {
            System.out.println("reconnect with process: " + procID);
            alivePortList.add(procID);
        }
    }

    private void startMonitorThread() {
        new Thread(()-> {
            while (true) {
                Long currentTime = System.currentTimeMillis();
                for (Integer procID: heartbeatUpdateTimestampMap.keySet()) {
                    if (currentTime - heartbeatUpdateTimestampMap.get(procID) > Configuration.getHeartbeatPeriod() * 2) {
                        System.out.println("process: " + procID + " timeout!");
                        heartbeatUpdateTimestampMap.remove(procID);
                        alivePortList.remove(procID);
                    }
                }
                try {
                    Thread.sleep(Configuration.getHeartbeatPeriod() * 2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * This thread can send heartbeat messages in portList periodically.
     */
    private void startSendThread() {
        new Thread(()-> {
            System.out.println("Heartbeat listener for process [" + rbp.procID +"] starts successfully");
            while (true) {
                for (Integer port: alivePortList) {
                    try (DatagramSocket socket = new DatagramSocket()) {
                        byte[] buf = new HeartbeatMessage(String.valueOf(procID)).toString().getBytes();
                        InetAddress host = InetAddress.getByName(Configuration.getHost());
                        DatagramPacket request = new DatagramPacket(buf, buf.length, host, port);
                        socket.send(request);
                        System.out.println("request sent: [" + new String(request.getData()) + "]   destination port: " +request.getPort());
                    } catch (IOException e) {
                        e.printStackTrace();
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

    private int[] getHeartbeatPortArrayByProcID(int procID) {
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
}
