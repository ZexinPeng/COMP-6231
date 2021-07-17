package main.server;

import javax.jws.WebMethod;
import javax.jws.WebService;
import java.rmi.Remote;

@WebService
public interface CenterServer extends Remote {
    @WebMethod(operationName="createTRecord")
    String createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location, String managerID);
    @WebMethod(operationName="createSRecord")
    String createSRecord(String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerID);
    /*
    if MTL has 6 records, LVL has 7 and DDO had 8, it should return the following: MTL 6, LVL 7, DDO 8.
     */
    @WebMethod(operationName="getRecordCounts")
    String getRecordCounts(String manageID);
    @WebMethod(operationName="editRecord")
    String editRecord(String recordID, String fieldName, String newValue, String managerID);
    @WebMethod(operationName="transferRecord")
    String transferRecord(String managerID, String recordID, String remoteCenterServerName);
}
