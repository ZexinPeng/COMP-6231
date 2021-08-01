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
        while (true) {
            try (DatagramSocket socket = new DatagramSocket(procID)) {
                socket.setSoTimeout(Configuration.getHeartbeatPeriod() * 2);
                byte[] buffer = new byte[50];
                while (true) {
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    socket.receive(request);
                    Message message = new Message(new String(request.getData()).trim());
                    if (message.getType().equals("heartbeat")) {
                        fbp.heartbeatThread.updateTimestamp(Integer.parseInt(message.getSenderID()));
                    } else {
                        System.out.println("unknown message type: " + message.getType());
                    }
                }
            } catch (IOException e) {
                fbp.heartbeatThread.allReplicationsHaveFailed();
            }
        }
    }
}
