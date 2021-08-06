package replication;

import replication.message.*;
import util.Configuration;
import util.Tool;

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
                    byte[] buffer = new byte[200];
                    while (true) {
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                        socket.receive(request);
                        String rawMessage = new String(request.getData()).trim();
                        Message message = new Message(rawMessage);
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
                            // send header process to front end
                            Tool.sendMessage(new HeaderMessage(fbp.procID, fbp.getHeader()).toString(), Configuration.getHost(), fbp.getFrontEndPort());
                            System.out.println("new header process: " + message.getSenderID());
                        }
                        else if (message.getType().equals(HeaderMessage.PREFIX)) {
                            Tool.sendMessage(new HeaderMessage(fbp.procID, fbp.getHeader()).toString(), Configuration.getHost(), fbp.getFrontEndPortByLocation(message.getSenderID()));
                        }
                        else if (message.getType().equals(RecordCountsMessage.PREFIX)) {
                            Tool.sendMessage(fbp.getRecordCounts(), request.getAddress().getHostAddress(), request.getPort());
                        }
                        else if (message.getType().equals(CreateSRecordMessage.PREFIX)) {
                            fbp.broadcast(CreateSRecordMessage.PREFIX, message.getContent());
                            Tool.sendMessage(fbp.createSRecord(message.getContent()), request.getAddress().getHostAddress(), request.getPort());
                        }
                        else if (message.getType().equals(CreateTRecordMessage.PREFIX)) {
                            fbp.broadcast(CreateTRecordMessage.PREFIX, message.getContent());
                            Tool.sendMessage(fbp.createTRecord(message.getContent()), request.getAddress().getHostAddress(), request.getPort());
                        }
                        else if (message.getType().equals(EditRecordMessage.PREFIX)) {
                            fbp.broadcast(EditRecordMessage.PREFIX, message.getContent());
                            Tool.sendMessage(fbp.editRecord(message.getContent()), request.getAddress().getHostAddress(), request.getPort());
                        }
                        else if (message.getType().equals(RemoveRecordMessage.PREFIX)) {
                            fbp.broadcast(RemoveRecordMessage.PREFIX, message.getContent());
                            Tool.sendMessage(fbp.removeRecord(message.getContent()), request.getAddress().getHostAddress(), request.getPort());
                        }
                        else if (message.getType().equals(InsertRecordMessage.PREFIX)) {
                            fbp.broadcast(InsertRecordMessage.PREFIX, message.getContent());
                            Tool.sendMessage(fbp.insertRecord(message.getContent()), request.getAddress().getHostAddress(), request.getPort());
                        }
                        else {
                            System.out.println("unknown message type: " + message.getType());
                        }
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
