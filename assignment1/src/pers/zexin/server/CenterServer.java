package pers.zexin.server;

import pers.zexin.bean.Location;

import java.rmi.Remote;

public interface CenterServer extends Remote {
    boolean createTRecord(String firstName, String lastName, String address, String phone, String specialization, Location location) throws java.rmi.RemoteException;
    boolean createSRecord(String firstName, String lastName, String[] courseRegistered, String status, String statusDate) throws java.rmi.RemoteException;
    /*
    if MTL has 6 records, LVL has 7 and DDO had 8, it should return the following: MTL 6, LVL 7, DDO 8.
     */
    String getRecordCounts() throws java.rmi.RemoteException;
    boolean editRecord(String recordID, String fieldName, String newValue) throws java.rmi.RemoteException;
}
