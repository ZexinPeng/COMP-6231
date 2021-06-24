package server;

import factory.ConfigurationFactory;
import util.Configuration;
import util.Tool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.atomic.AtomicInteger;

public class IDServer {
    private static AtomicInteger studentNum = new AtomicInteger(0);
    private static AtomicInteger teacherNum = new AtomicInteger(0);

    private static Configuration configuration = ConfigurationFactory.getConfiguration();

    public static void main(String[] args) {
        startStudentIdThread();
        startTeacherIdThread();
    }

    private static void startStudentIdThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int port = configuration.getStudentPortID();
                DatagramSocket aSocket = null;
                try{
                    aSocket = new DatagramSocket(port);
                    // create socket at agreed port
                    byte[] buffer = new byte[1000];
                    System.out.println("Student ID service starts.");
                    while(true){
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                        aSocket.receive(request);
                        DatagramPacket reply = new DatagramPacket(Tool.int2ByteArray(studentNum.incrementAndGet()), 4,
                                request.getAddress(), request.getPort());
                        aSocket.send(reply);
                        System.out.println("reply to " + request.getAddress() + ":" +request.getPort() + " with studentNum " + Tool.bytes2Int(reply.getData()));
                    }
                }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
                }catch (IOException e) {System.out.println("IO: " + e.getMessage());
                }finally {if(aSocket != null) aSocket.close();}
            }
        }).start();
    }

    private static void startTeacherIdThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int port = configuration.getTeacherPortID();
                DatagramSocket aSocket = null;
                try{
                    aSocket = new DatagramSocket(port);
                    // create socket at agreed port
                    byte[] buffer = new byte[1000];
                    System.out.println("Teacher ID service starts.");
                    while(true){
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                        aSocket.receive(request);
                        DatagramPacket reply = new DatagramPacket(Tool.int2ByteArray(teacherNum.incrementAndGet()), 4,
                                request.getAddress(), request.getPort());
                        aSocket.send(reply);
                        System.out.println("reply to " + request.getAddress() + ":" +request.getPort() + " with teacherNum " + Tool.bytes2Int(reply.getData()));
                    }
                }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
                }catch (IOException e) {System.out.println("IO: " + e.getMessage());
                }finally {if(aSocket != null) aSocket.close();}
            }
        }).start();
    }
}
