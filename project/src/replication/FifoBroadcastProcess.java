package replication;


import bean.Location;
import replication.election.ElectionThread;
import replication.heartbeat.HeartbeatListenerThread;
import replication.message.*;
import util.Configuration;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.*;

public class FifoBroadcastProcess extends ReplicationImpl
{
   //We must track which messages we have received.
   private List<BroadCastMessage> messagesReceived;

   //Number of messages initially sent by this process.
   protected int messageSentCount;

   //Connection details for this implementation.
   private InetAddress address;
   private MulticastSocket socket;
   private static int SOCKET;

   //Messages that have been R-delivered (meaning that they were delivered
   // by Reliable Broadcast) but have not yet been delayed by this algorithm.
   protected List<BroadCastMessage> msgBag;
   
   //Records the sequence number of the next message that
   // we are expecting to see from the corresponding process.
   protected Map<String,Integer> messageSeqMap;

   protected HeartbeatListenerThread heartbeatThread;

   protected ElectionThread electionThread;

   private MessageRouter messageRouter;

   // the port of the head of the replication group
   private String header = "null";
   
   /**
      Constructor
    */
   public FifoBroadcastProcess(String ID, String groupAddress, int port)
   {
      procID = ID;
      messageSentCount = 0;
      messagesReceived = new ArrayList();
      SOCKET = port;
      try
      {
         socket = new MulticastSocket(SOCKET);
         address = InetAddress.getByName(groupAddress);
         socket.joinGroup(address);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      msgBag = new ArrayList();
      messageSeqMap = new HashMap();
   }

   public FifoBroadcastProcess(String ID, int port)
   {
      this(ID, "230.0.0.1", port);
   }

   /**
    Method that represents the receive primitive, following the
    text's algorithm.

    @param m  BroadCastMessage received.
    */
   public void receive(BroadCastMessage m)
   {
      //If the message has been received before, it is then ignored.
      if (!messagesReceived.contains(m))
      {
         //We must track messages that we receive so that we do not
         // relay them multiple times.
         messagesReceived.add(m);

         //Note that the message is relayed to all processes
         // before it is delivered.
         sendToAll(m);
         deliver(m);
      }
   }
   
   /**
      This represents the delivery of a message from Reliable
      Broadcast (aka R-delivery).  This will not be delivered
      to the system by a FIFO process (aka F-Delivery) until
      all previous messages from the sending proccess have
      been received.
    */
   public void deliver(BroadCastMessage m)
   {
      //Process is put in a queue
      msgBag.add(m);
      
      //Unlike the text's algorithm, we only keep track of the number
      // of messages sent from a process from the first time that we
      // receive a broadcast message from it.  This frees us from
      // having to keep a specific list of which processes this
      // one may communicate with.
      if (!messageSeqMap.containsKey(m.getSenderID()))
      {
         messageSeqMap.put(m.getSenderID(), 0);
      }
      
      //After receiving a new message, we deliver all
      // messages to the system that we now can.
      fDeliverAllPossibleMessages(m.getSenderID());
   }
   
   /**
      If a message is delivered, and any messages were
      queued up waiting for its arrival, this method
      will deliver all of them.
    */
   private void fDeliverAllPossibleMessages(String sender)
   {
      boolean newDelivery = false;
      for (Iterator<BroadCastMessage> iter = msgBag.iterator(); iter.hasNext(); )
      {
         BroadCastMessage m = iter.next();
         int expected = messageSeqMap.get(m.getSenderID());
         if (m.getSequenceNumber()==expected && m.getSenderID().equals(sender))
         {
            fDeliver(m);
            messageSeqMap.put(m.getSenderID(), expected+1);
            iter.remove();
            newDelivery = true;
         }
      }
      //If a message was delivered in the last pass, we must cycle
      // through them again to ensure that no messages waiting for
      // THAT message's delivery are still waiting needlessly.
      if (newDelivery) fDeliverAllPossibleMessages(sender);
   }
   
   /**
      When this method is called, the message has finally
      been F-delivered to the process.
    */
   protected void fDeliver(BroadCastMessage m)
   {
      route(m);
   }

   /**
    Starts the process running.  It will read from standard input and broadcast
    messages for text that the user enters.  Also, this will start the listener
    thread so that it will receive messages broadcast and heartbeat messages by other processes.
    */
   public void start()
   {
      startRouter();
      try {
         Thread.sleep(1000);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
      startListenerThread();
      startHeartbeatThread();
      startFirstElection();
   }

   protected void startFirstElection() {
      electionThread = new ElectionThread(this);
      electionThread.startFirstElection();
   }

   /**
    Starts a new thread to listen for broadcast messages.
    */
   protected void startListenerThread()
   {
      FifoBroadcastListenerThread thread = new FifoBroadcastListenerThread(this, address, SOCKET);
      System.out.println("broadcast thread starts successfully!");
      thread.start();
   }

   protected void startHeartbeatThread() {
      heartbeatThread = new HeartbeatListenerThread(this);
      heartbeatThread.start();
   }

   // This method can parse the received messages and parse them.
   protected void startRouter() {
      messageRouter = new MessageRouter(this);
      messageRouter.startRouter();
   }

   /**
    This broadcasts a new message.  Note that at creation,
    the message is tagged with the process's ID and the
    sequence number of this message.
    */
   public void broadcast(String type, String content)
   {
      BroadCastMessage m = new BroadCastMessage(type, procID, String.valueOf(messageSentCount++), content);
      sendToAll(m);
   }

   /**
    Method that represents the send primitive.  It transmits a
    message to all processes, including itself.  Note that the text differed
    slightly from this in that its send primitive was responsible
    only for transmitting a message to a single process, rather
    than transmitting to all processes.  To capture this difference,
    we have renamed this "sendToAll".

    @param m  BroadCastMessage to transmit.
    */
   public void sendToAll(BroadCastMessage m)
   {
      byte[] buf = m.toString().getBytes();
      try
      {
         DatagramPacket packet = new DatagramPacket(buf, buf.length, address, SOCKET);
         socket.send(packet);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private void route(Message message) {
      if (message.getType().equals(CreateSRecordMessage.PREFIX)) {
         createSRecord(message.getContent());
      }
      else if (message.getType().equals(CreateTRecordMessage.PREFIX)) {
         createTRecord(message.getContent());
      }
      else if (message.getType().equals(EditRecordMessage.PREFIX)) {
         editRecord(message.getContent());
      }
      else if (message.getType().equals(RemoveRecordMessage.PREFIX)) {
         removeRecord(message.getContent());
      }
      else if (message.getType().equals(InsertRecordMessage.PREFIX)) {
         insertRecord(message.getContent());
      }
      else {
         System.out.println("unknown broadCastMessage type: " + message.getType());
      }
   }

   /**
      Calls the start method for this class.
    */
   public static void start(String procID, int port)
   {
      if (procID == null || port <= 0)
      {
         System.out.println("process ID [" + procID + "] or port [ " + port +" ] is invalid");
         System.exit(0);
      }
      FifoBroadcastProcess fbp = new FifoBroadcastProcess(procID, port);
      fbp.start();
   }

   public String getHeader() {
      return header;
   }

   public void setHeader(String header) {
      this.header = header;
   }

   public ElectionThread getElectionThread() {
      return electionThread;
   }

   public HeartbeatListenerThread getHeartbeatThread() {
      return heartbeatThread;
   }

   public int getFrontEndPort() {
      int procID = Integer.parseInt(this.procID);
      int[] ports = Configuration.getLvlHeartbeatPorts();
      for (int i = 0; i < ports.length; i++) {
         if (procID == ports[i]) {
            return Configuration.getLvlPort();
         }
      }
      ports = Configuration.getDdoHeartbeatPorts();
      for (int i = 0; i < ports.length; i++) {
         if (procID == ports[i]) {
            return Configuration.getDdoPort();
         }
      }
      return Configuration.getMtlPort();
   }

   public int getFrontEndPortByLocation(String location) {
      if (location.equals(Location.LVL.toString())) {
         return Configuration.getLvlPort();
      } else if (location.equals(Location.DDO.toString())) {
         return Configuration.getDdoPort();
      }
      return Configuration.getMtlPort();
   }
}
