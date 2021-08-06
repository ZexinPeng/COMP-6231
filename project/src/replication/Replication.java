package replication;

public interface Replication {
    String createTRecord(String message);
    String createSRecord(String message);
    /*
    if MTL has 6 records, LVL has 7 and DDO had 8, it should return the following: MTL 6, LVL 7, DDO 8.
     */
    String getRecordCounts();
    String editRecord(String message);
    String removeRecord(String messageContent);
    String insertRecord(String messageContent);
}
