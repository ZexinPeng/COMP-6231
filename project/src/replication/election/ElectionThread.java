package replication.election;

import replication.FifoBroadcastProcess;
import util.Configuration;
import util.Tool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.List;

public class ElectionThread{

    private final FifoBroadcastProcess rbp;

    List<Integer> alivePortList;

    private volatile Long coordinatorTimestamp = 0L;

    public ElectionThread(FifoBroadcastProcess rbp) {
        this.rbp = rbp;
        alivePortList = rbp.getHeartbeatThread().getAlivePortList();
    }

    public void startFirstElection() {
        if (Integer.parseInt(rbp.procID) >= alivePortList.get(alivePortList.size() - 1)) {
            sendCoordinatorMessage();
            setCoordinator(rbp.procID);
        } else {
            startElection();
        }
    }

    public void startElection() {
        System.out.println("process [" + rbp.procID +  "] starts election!");
        int procID = Integer.parseInt(rbp.procID);
        int electionPort = procID + Configuration.getElectionPortInterval();
        boolean isHeader = true;
        List<Election> electionList = new LinkedList<>();
        synchronized (alivePortList) {
            for (Integer port: alivePortList) {
                if (procID > port) {
                    continue;
                }
                isHeader = false;
                electionList.add(new Election(rbp.procID, port));
            }
        }
        // wait for answer messages
        Long timeMill = System.currentTimeMillis();
        if (!isHeader) {
            System.out.println(System.currentTimeMillis() + " listen on port: " + electionPort);
            try (DatagramSocket socket = new DatagramSocket(electionPort)) {
                socket.setSoTimeout(Configuration.getElectionTimeout());
                for (Election election: electionList) {
                    Tool.sendMessage(election.toString(), Configuration.getHost(), election.getDestination());
                }
                while (true) {
                    if (coordinatorTimestamp != 0) {
                        return;
                    }
                    if (System.currentTimeMillis() - timeMill >= 3 * Configuration.getElectionTimeout()) {
                        removeHigherReplications();
                        startElection();
                    }
                    byte[] buffer = new byte[30];
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    System.out.println("ssssssssssssssssssss:::" + new String(request.getData()).trim());
                }
            } catch (IOException e) {
                if (coordinatorTimestamp != 0) {
                    return;
                }
                // no answer message from higher replications, this replication is the coordinator
                System.out.println("no answer message from higher replications");
                removeHigherReplications();
                sendCoordinatorMessage();
                setCoordinator(rbp.procID);
            }
        } else {
            // This replication has the highest procID
            sendCoordinatorMessage();
            setCoordinator(rbp.procID);
        }
    }

    public void sendAnswerMessage(String senderID) {
        Tool.sendMessage(new Answer(rbp.procID).toString(), Configuration.getHost(), getElectionPort(Integer.parseInt(senderID)));
    }

    public int getElectionPort(int port) {
        return port + Configuration.getElectionPortInterval();
    }

    private void removeHigherReplications() {
        int procID = Integer.parseInt(rbp.procID);
        synchronized (alivePortList) {
            for (int i = 0; i < alivePortList.size(); i++) {
                if (procID < alivePortList.get(i)) {
                    alivePortList.remove(i);
                    i--;
                }
            }
        }
    }

    private void sendCoordinatorMessage() {
        synchronized (alivePortList) {
            for (Integer port: alivePortList) {
                Tool.sendMessage(new Coordinator(rbp.procID).toString(), Configuration.getHost(), port);
            }
        }
    }

    private void setCoordinator(String procID) {
        rbp.setHeader(procID);
        System.out.println("new header process: " + procID);
    }

    public Long getCoordinatorTimestamp() {
        return coordinatorTimestamp;
    }

    public void setCoordinatorTimestamp(Long coordinatorTimestamp) {
        this.coordinatorTimestamp = coordinatorTimestamp;
    }
}
