package pers.zexin.client;

import pers.zexin.bean.Location;

// initial records id "TR00005", "SR00006", "TR00007", "TR00008"
public class DDOClient extends ManagerClient{
    public static void main(String[] args) {
        initiate("0001", Location.DDO);
        // scenario 1
//        startGetRecordCounts();
//        startCreateTRecordClient();
//        startCreateSRecordClient();
//        startGetRecordCounts();
        // scenario 2.c
//        startEditRecord("SR00006", "status", "wrongInput");
        // scenarios 2.f
//        startEditRecord("TR00005", "phone", "15689477162");
        // scenario 2.i
        startEditRecord("SR00006", "status", "inactive");
    }
}
