package pers.zexin.client;

import pers.zexin.bean.Location;

// initial records TR00003", "SR00004"
public class MTLClient extends ManagerClient{
    public static void main(String[] args) {
        initiate("0001", Location.MTL);
        // scenario 1
//        startGetRecordCounts();
//        startCreateTRecordClient();
//        startCreateSRecordClient();
//        startCreateSRecordClient();
//        startGetRecordCounts();

        // scenario 2.b
//        startEditRecord("TR00003", "wrongFieldName", "mockValue");
        // scenario 2.e
//        startEditRecord("TR00003", "address", "Shanghai Road, Urumqi, China");
        // scenario 2.h
//        startEditRecord("SR00004", "courseRegistered", "math,english");
        // scenario 2.k
        startEditRecord("SR00004","statusDate", "2021-06-12 14:26:59");
    }
}
