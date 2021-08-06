package replication;

import util.Configuration;

/**
 Represents a single message, and handles the details of
 the made-up protocol that we are using.

 */
public class BroadCastMessage extends Message
{
    private int sequenceNum;

    public BroadCastMessage(String[] msg) {
        super(msg[0], msg[1], msg[3]);
        this.sequenceNum = Integer.parseInt(msg[2]);
    }

    public BroadCastMessage(String type, String senderID, String sequenceNum, String content) {
        super(type, senderID, content);
        this.sequenceNum = Integer.parseInt(sequenceNum);
    }

    public String toString() {
        return type + Configuration.getSeparator() + senderID + Configuration.getSeparator() + sequenceNum + Configuration.getSeparator() + content + Configuration.getSeparator();
    }

    /**
     This will take a transmissionString (in our made up protocol)
     and construct a new message object from it.

     @param receivedText  Text received from a broadcast message.
     */
    public static BroadCastMessage parseTransmissionString(String receivedText)
    {
        return new BroadCastMessage(receivedText.split(Configuration.getSeparator())[0]
        , receivedText.split(Configuration.getSeparator())[1]
        , receivedText.split(Configuration.getSeparator())[2]
        , receivedText.split(Configuration.getSeparator())[3]);
    }

    /**
     @return  ID of the original broadcaster of this message.
     */
    public String getSenderID()
    {
        return senderID;
    }

    /**
     @return  The sequence number of this message.
     */
    public int getSequenceNumber()
    {
        return sequenceNum;
    }

    /**
     Two messages are equal if the sender and sequence number are the same.

     @see Object#equals(Object)
     */
    public boolean equals(Object o)
    {
        if (!(o instanceof BroadCastMessage)) return false;
        BroadCastMessage other = (BroadCastMessage)o;
        return this.senderID.equals(other.senderID) && this.sequenceNum==other.sequenceNum;
    }

    public String getType() {
        return type;
    }

    /**
     Test main method for this class.  You can run this to verify
     that a message is not distorted after its reconstruction.
     */
//    public static void main(String [] args)
//    {
//        BroadCastMessage m1 = new BroadCastMessage("Hola.", "Alpha", 0);
//        System.out.println(m1.transmissionString());
//        BroadCastMessage m2 = BroadCastMessage.parseTransmissionString(m1.transmissionString());
//        System.out.println("m1 eq? m2: " + m1.equals(m2));
//    }
}
