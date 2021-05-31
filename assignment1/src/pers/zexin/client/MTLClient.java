package pers.zexin.client;

import pers.zexin.bean.Location;

public class MTLClient extends ManagerClient{
    public static void main(String[] args) {
        // scenario 1
        startGetRecordCounts(Location.MTL);
        startCreateTRecordClient(Location.MTL);
        startCreateSRecordClient(Location.MTL);
        startGetRecordCounts(Location.MTL);
    }
}
