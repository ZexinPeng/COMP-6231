package client;

import bean.Location;

public class TestScenario5b {
    public static void main(String[] args) {
        Client client = new Client();
        client.initialClient(new String[]{}, Location.LVL.toString(), 1);
        String logMessage = client.serverImpl.transferRecord(client.managerID, "TR00003", "InvalidLocation");
        client.generateLog(logMessage);
    }
}
