package pers.zexin.client;

import pers.zexin.bean.Location;

public class ManagerClient {
    public boolean createTRecord (String firstName, String lastName, String address, String phone, String specialization, Location location) {
        return false;
    }

    public boolean createSRecord (String firstName, String lastName, String[] courseRegistered, String status, String statusDate) {
        return false;
    }

    // if MTL has 6 records, LVL has 7 and DDO had 8, it should return the following: MTL 6, LVL 7, DDO 8.
    public String getRecordCounts () {
        return null;
    }

    public boolean editRecord (String recordID, String fieldName, String newValue) {
        return false;
    }
}
