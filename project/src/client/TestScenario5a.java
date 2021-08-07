package client;

import bean.Location;

public class TestScenario5a {
    public static void main(String[] args) {
        Client client = new Client();
        client.initialClient(new String[]{}, Location.LVL.toString(), 1);
        String logMessage = client.serverImpl.transferRecord(client.managerID, "invalidID", Location.DDO.toString());
        client.generateLog(logMessage);
    }
}
