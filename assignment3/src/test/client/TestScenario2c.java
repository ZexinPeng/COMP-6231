package test.client;

import main.bean.Location;

public class TestScenario2c {
    public static void main(String[] args) {
        Client client = new Client();
        client.setManagerID(Location.LVL.toString(), 1);
        System.out.println(Client.LVLServer.getRecordCounts(client.managerID));
        Thread a = new Thread(new Thread1());
        Thread b = new Thread(new Thread2());
        Thread c = new Thread(new Thread3());
        a.start();
        b.start();
        c.start();
        while (a.isAlive() || b.isAlive() || c.isAlive()) {

        }
        System.out.println(Client.LVLServer.getRecordCounts(client.managerID));
    }

    static class Thread1 implements Runnable{
        @Override
        public void run() {
            Client client = new Client();
            client.setManagerID(Location.DDO.toString(), 1);
            String logMessage = client.DDOServer.transferRecord(client.managerID, "TR00004", Location.LVL.toString());
            client.generateLog(logMessage);
        }
    }

    static class Thread2 implements Runnable{
        @Override
        public void run() {
            Client client = new Client();
            client.setManagerID(Location.DDO.toString(), 2);
            String logMessage = client.DDOServer.transferRecord(client.managerID, "TR00004", Location.LVL.toString());
            client.generateLog(logMessage);
        }
    }

    static class Thread3 implements Runnable{
        @Override
        public void run() {
            Client client = new Client();
            client.setManagerID(Location.DDO.toString(), 3);
            String logMessage = client.DDOServer.transferRecord(client.managerID, "TR00004", Location.LVL.toString());
            client.generateLog(logMessage);
        }
    }
}
