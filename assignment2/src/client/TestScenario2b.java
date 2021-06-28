package client;

import bean.Location;

public class TestScenario2b {
    public static void main(String[] args) {
        Client client = new Client();
        client.initialClient(new String[]{}, Location.MTL.toString(), 1);
        String logMessage = client.serverImpl.transferRecord(client.managerID, "TR00003", "InvalidLocation");
        client.generateLog(logMessage);
    }
}
