package util;

import replication.heartbeat.HeartbeatMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Tool {
    private static boolean debug = false;
    /**
     * This method will write the content into the log file
     * @param p_content the content of the log message
     */
    public static void write2LogFile(String p_content, String directory, String fileName) {
        //create file writer
        FileWriter fw = null;
        try {
            File dir = new File(directory);
            if (!dir.isDirectory()) {
                dir.mkdirs();
            }
            File f = new File(dir, fileName);
            if (!f.isFile()) {
                f.createNewFile();
            }
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //create print writer
        PrintWriter l_pw = new PrintWriter(fw);
        l_pw.println(p_content);
        l_pw.flush();
        try {
            fw.flush();
            l_pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printError(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static byte[] int2ByteArray(int integer) {
        byte[] bytes=new byte[4];
        bytes[3]=(byte) (integer >> 24);
        bytes[2]=(byte) (integer >> 16);
        bytes[1]=(byte) (integer >> 8);
        bytes[0]=(byte) integer;
        return bytes;
    }

    public static int bytes2Int(byte[] bytes ) {
        //如果不与0xff进行按位与操作，转换结果将出错，有兴趣的同学可以试一下。
        int int1 = bytes[0]&0xff;
        int int2 = (bytes[1]&0xff)<<8;
        int int3 = (bytes[2]&0xff)<<16;
        int int4 = (bytes[3]&0xff)<<24;
        return int1 | int2 | int3 | int4;
    }

    /**
     * return if the time is in the format of yyyy-MM-dd HH:mm:ss
     * @param time
     * @return
     */
    public static boolean isDateFormatValid(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            simpleDateFormat.parse(time);
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static void sendMessage(String message, String host, int port) {
        try (DatagramSocket socket = new DatagramSocket()) {
            byte[] buf = message.getBytes();
            InetAddress inetAddress = InetAddress.getByName(host);
            DatagramPacket request = new DatagramPacket(buf, buf.length, inetAddress, port);
            socket.send(request);
            if (!debug) {
                System.out.println("request sent: " + new String(request.getData()).trim() + ";destination port: " +request.getPort()
                        + ";timestamp: " + System.currentTimeMillis());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String sendMessageWithReply(String message, String host, int port) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setSoTimeout(Configuration.getShortTimeout());
            byte[] buf = message.getBytes();
            InetAddress inetAddress = InetAddress.getByName(host);
            DatagramPacket request = new DatagramPacket(buf, buf.length, inetAddress, port);
            socket.send(request);
            if (!debug) {
                System.out.println("request sent: " + new String(request.getData()).trim() + ";destination port: " +request.getPort()
                        + ";timestamp: " + System.currentTimeMillis());
            }
            byte[] bufffer = new byte[200];
            DatagramPacket datagramPacket = new DatagramPacket(bufffer, bufffer.length);
            socket.setSoTimeout(Configuration.getShortTimeout());
            socket.receive(datagramPacket);
            return new String(datagramPacket.getData()).trim();
        } catch (IOException e) {
            return null;
        }
    }

    public static void printArr(int[] objects) {
        StringBuilder sb = new StringBuilder();
        for (Object obj: objects) {
            sb.append(obj).append(", ");
        }
        System.out.println(sb);
    }
}
