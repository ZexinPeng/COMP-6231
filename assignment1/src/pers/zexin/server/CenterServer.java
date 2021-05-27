package pers.zexin.server;

import pers.zexin.bean.Location;
import pers.zexin.bean.Manager;
import pers.zexin.bean.TeacherRecord;

import java.rmi.Remote;

public interface CenterServer extends Remote {
    TeacherRecord createTRecord(String firstName, String lastName, String address, String phone, String specialization, Location location, Manager manager) throws java.rmi.RemoteException;
    boolean createSRecord(String firstName, String lastName, String[] courseRegistered, String status, String statusDate, Manager manager) throws java.rmi.RemoteException;
    /*
    if MTL has 6 records, LVL has 7 and DDO had 8, it should return the following: MTL 6, LVL 7, DDO 8.
     */
    String getRecordCounts(Manager manager) throws java.rmi.RemoteException;
    boolean editRecord(String recordID, String fieldName, String newValue, Manager manager) throws java.rmi.RemoteException;
}
