package test.client;

import main.bean.Location;

public class TestScenario2b {
    public static void main(String[] args) {
        Client client = new Client();
        client.setManagerID(Location.MTL.toString(), 1);
        String logMessage = client.MTLServer.transferRecord(client.managerID, "TR00003", "InvalidLocation");
        client.generateLog(logMessage);
    }
}
