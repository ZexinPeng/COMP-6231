package replication;

import util.Configuration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MessageRouter {
    private final int procID;

    FifoBroadcastProcess fbp;

    public MessageRouter(FifoBroadcastProcess fbp) {
        this.fbp = fbp;
        procID = Integer.parseInt(fbp.procID);
    }

    // This method can parse the received messages and parse them.
    protected void startRouter() {
        new Thread(()->{
            while (true) {
                try (DatagramSocket socket = new DatagramSocket(procID)) {
                    socket.setSoTimeout(Configuration.getHeartbeatPeriod() * 2);
                    byte[] buffer = new byte[30];
                    while (true) {
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                        socket.receive(request);
                        Message message = new Message(new String(request.getData()).trim());
                        printMessageInfo(request);
                        if (message.getType().equals("heartbeat")) {
                            fbp.heartbeatThread.updateTimestamp(Integer.parseInt(message.getSenderID()));
                        } else if (message.getType().equals("election")) {
                            fbp.heartbeatThread.updateTimestamp(Integer.parseInt(message.getSenderID()));
                            fbp.electionThread.sendAnswerMessage(message.getSenderID());
                            fbp.electionThread.startElection();
                        } else if (message.getType().equals("coordinator")) {
                            fbp.getElectionThread().setCoordinatorTimestamp(System.currentTimeMillis());
                            fbp.setHeader(message.getSenderID());
                            System.out.println("new header process: " + message.getSenderID());
                        }
                        else {
                            System.out.println("unknown message type: " + message.getType());
                        }
                        printHeader();
                    }
                } catch (IOException e) {
                    fbp.heartbeatThread.allReplicationsHaveFailed();
                }
            }
        }).start();
    }

    private void printMessageInfo(DatagramPacket request) {
        System.out.println("timestamp:"+System.currentTimeMillis()+";content: " + new String(request.getData()).trim());
    }

    private void printHeader() {
        System.out.println("current Header: " + fbp.getHeader());
    }
}
