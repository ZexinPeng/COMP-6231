package client;

import bean.Location;

public class TestScenario4c {
    public static void main(String[] args) {
        Client client = new Client();
        client.initialClient(new String[]{}, Location.LVL.toString(), 1);
        String logMessage = client.serverImpl.editRecord("TR00001", "address", "newAddress", client.managerID);
        client.generateLog(logMessage);
    }
}
