package server;

import ServerApp.ServerPOA;

public class ServerImpl extends ServerPOA {
    @Override
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location, String managerID) {
        return "123";
    }

    @Override
    public String createSRecord(String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerID) {
        return "123";
    }

    @Override
    public String getRecordCounts(String managerID) {
        return "123";
    }

    @Override
    public String editRecord(String recordID, String fieldName, String newValue, String managerID) {
        return "123";
    }

    @Override
    public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
        return "123";
    }
}
