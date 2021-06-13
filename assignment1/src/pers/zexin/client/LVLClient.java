package pers.zexin.client;

import pers.zexin.bean.Location;
import pers.zexin.util.Tool;

// initial record SR00002 TR00001 TR00000
public class LVLClient extends ManagerClient{
    public static void main(String[] args) {
        initiate("0001", Location.LVL);
        // scenario 1
//        startGetRecordCounts();
//        startCreateTRecordClient();
//        startCreateSRecordClient();
//        startGetRecordCounts();

        // scenario 2.a
//        startEditRecord("SR00010", "status", "inactive");
        // scenario 2.d
//        startEditRecord("TR00001", "location", "invalidLocation");
        // scenario 2.g
//        startEditRecord("TR00001", "location", "DDO");
        // scenarios 2.j
        startEditRecord("SR00002","statusDate", "202106-50 14:26:59");
    }
}
