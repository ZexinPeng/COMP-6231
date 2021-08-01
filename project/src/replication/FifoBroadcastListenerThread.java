package replication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


/**
 This class listens for broadcast messages and reports them to the
 ReliableBroadcastProcss that spawned it.
 */
public class FifoBroadcastListenerThread extends Thread
{
    //RB process that this class corresponds to.
    private FifoBroadcastProcess rbp;

    //Network connection details.
    private int socketNum;
    private InetAddress address;

    /**
     Constructor.

     @param rbp  Process that this thread belongs to.
     @param address  Address of the broadcast group.
     @param socketNum  Socket on which to listen.
     */
    public FifoBroadcastListenerThread(FifoBroadcastProcess rbp, InetAddress address, int socketNum)
    {
        this.rbp = rbp;
        this.address = address;
        this.socketNum = socketNum;
    }

    /**
     Starts the thread.  It will listen for broadcast messages and report
     them to its corresponding process.

     @see Runnable#run()
     */
    public void run()
    {
        MulticastSocket socket;
        try
        {
            socket = new MulticastSocket(socketNum);
            socket.joinGroup(address);

            DatagramPacket packet;
            while (true)
            {
                byte[] buf = new byte[256];
                packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);
                String received = new String(packet.getData());

                //Here we parse the data received to construct a
                // new Message object from it.
                Message m = Message.parseTransmissionString(received.trim());

                //The message is passed to the RB process here.
                rbp.receive(m);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
