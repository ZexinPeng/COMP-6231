package test.client;

import main.bean.Location;

public class TestScenario2d {
    public static void main(String[] args) {
        Client client = new Client();
        client.setManagerID(Location.LVL.toString(), 4);
        System.out.println(Client.LVLServer.getRecordCounts(client.managerID));
        Thread a = new Thread(new editThread1());
        Thread b = new Thread(new editThread2());
        client.generateLog(Client.LVLServer.transferRecord(client.managerID, "TR00001", Location.DDO.toString()));
        Thread c = new Thread(new editThread3());
        a.start();
        b.start();
        c.start();
        while (a.isAlive() || b.isAlive() || c.isAlive()) {

        }
        System.out.println(Client.LVLServer.getRecordCounts(client.managerID));
    }

    static class editThread1 implements Runnable{
        @Override
        public void run() {
            Client client = new Client();
            client.setManagerID(Location.LVL.toString(), 1);
            String logMessage = Client.LVLServer.editRecord("TR00001", "address", "threadA", client.managerID);
            client.generateLog(logMessage);
        }
    }

    static class editThread2 implements Runnable{
        @Override
        public void run() {
            Client client = new Client();
            client.setManagerID(Location.LVL.toString(), 2);
            String logMessage = Client.LVLServer.editRecord("TR00001", "address", "threadB", client.managerID);
            client.generateLog(logMessage);
        }
    }

    static class editThread3 implements Runnable{
        @Override
        public void run() {
            Client client = new Client();
            client.setManagerID(Location.LVL.toString(), 3);
            String logMessage =  Client.LVLServer.editRecord("TR00001", "address", "threadC", client.managerID);
            client.generateLog(logMessage);
        }
    }
}
