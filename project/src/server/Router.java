package server;

import bean.Location;
import replication.message.HeaderMessage;
import replication.message.Message;
import util.Configuration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Router {
    ServerImpl server;
    private Location location;

    public Router(ServerImpl server) {
        this.server = server;
        this.location = server.location;
    }

    public void startRouter() {
        new Thread(()-> {
            while (true) {
                try (DatagramSocket socket = new DatagramSocket(getRouterPort())) {
                    byte[] buffer = new byte[200];
                    while (true) {
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                        socket.receive(request);
                        printMessageInfo(request);
                        String rawMessage = new String(request.getData()).trim();
                        Message message = new Message(rawMessage);
                        System.out.println(message);
                        if (message.getType().equals(HeaderMessage.PREFIX)) {
                            processHeaderMessage(message);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private int getRouterPort() {
        if (location.equals(Location.LVL)) {
            return Configuration.getLvlPort();
        } else if (location.equals(Location.DDO)) {
            return Configuration.getDdoPort();
        } else {
            return Configuration.getMtlPort();
        }
    }

    private void printMessageInfo(DatagramPacket request) {
        System.out.println("timestamp:"+System.currentTimeMillis()+";content: " + new String(request.getData()).trim());
    }

    private void processHeaderMessage(Message message) {
        int[] requestPorts = Configuration.getLvlHeartbeatPorts();
        int port = Integer.parseInt(message.getContent());
        for (int requestPort : requestPorts) {
            if (port == requestPort) {
                server.headerPorts[server.getHeaderIndex(Location.LVL)] = port;
                System.out.println("new " + Location.LVL + "Header: " + message.getContent());
                return;
            }
        }
        requestPorts = Configuration.getDdoHeartbeatPorts();
        for (int requestPort : requestPorts) {
            if (port == requestPort) {
                server.headerPorts[server.getHeaderIndex(Location.DDO)] = port;
                System.out.println("new " + Location.DDO + "Header: " + message.getContent());
                return;
            }
        }
        requestPorts = Configuration.getMtlHeartbeatPorts();
        for (int requestPort : requestPorts) {
            if (port == requestPort) {
                server.headerPorts[server.getHeaderIndex(Location.MTL)] = port;
                System.out.println("new " + Location.MTL + "Header: " + message.getContent());
                return;
            }
        }

    }
}
