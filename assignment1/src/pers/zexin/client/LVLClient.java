package pers.zexin.client;

import pers.zexin.bean.Location;

public class LVLClient extends ManagerClient{
    public static void main(String[] args) {
        // scenario 1
//        startGetRecordCounts(Location.LVL);
//        startCreateTRecordClient(Location.LVL);
//        startCreateSRecordClient(Location.LVL);
//        startGetRecordCounts(Location.LVL);

        // scenario 2
        startGetRecordCounts(Location.LVL);
        startCreateTRecordClient(Location.LVL);
        startCreateSRecordClient(Location.LVL);
        startGetRecordCounts(Location.LVL);
    }
}
