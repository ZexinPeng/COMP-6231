package pers.zexin.client;

import pers.zexin.bean.Location;

public class DDOClient extends ManagerClient{
    public static void main(String[] args) {
        // scenario 1
        startGetRecordCounts(Location.DDO);
        startCreateTRecordClient(Location.DDO);
        startCreateSRecordClient(Location.DDO);
        startGetRecordCounts(Location.DDO);
    }
}
