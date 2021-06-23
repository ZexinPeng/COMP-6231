package ServerApp;


/**
* ServerApp/ServerOperations.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from Server.idl
* Wednesday, June 23, 2021 2:15:39 PM CST
*/

public interface ServerOperations 
{
  String createTRecord (String firstName, String lastName, String address, String phone, String specialization, String location, String managerID);
  String createSRecord (String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerID);

  /*
        if MTL has 6 records, LVL has 7 and DDO had 8, it should return the following: MTL 6, LVL 7, DDO 8.
         */
  String getRecordCounts (String managerID);
  String editRecord (String recordID, String fieldName, String newValue, String managerID);
  String transferRecord (String managerID, String recordID, String remoteCenterServerName);
} // interface ServerOperations