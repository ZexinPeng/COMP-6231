package test.client;

import main.bean.Location;

public class TestScenario2a {
    public static void main(String[] args) {
        Client client = new Client();
        client.setManagerID(Location.LVL.toString(), 1);
        String logMessage = client.LVLServer.transferRecord(client.managerID, "invalidID", Location.DDO.toString());
        client.generateLog(logMessage);
    }
}
