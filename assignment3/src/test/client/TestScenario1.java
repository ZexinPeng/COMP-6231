package test.client;

import main.bean.Location;
import main.util.Tool;

public class TestScenario1 {
    public static void main(String[] args) {
        Thread LVLThread = new Thread(new LVLThread());
        Thread MTLThread = new Thread(new MTLThread());
        Thread DDOThread = new Thread(new DDOThread());
        LVLThread.start();
        MTLThread.start();
        DDOThread.start();
    }

    static class LVLThread extends Client implements Runnable{
        @Override
        public void run() {
            setManagerID(Location.LVL.toString(),1);
            String logMessage;
            for (int i = 0; i < 100; i++) {
                logMessage = LVLServer.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), managerID);
                generateLog(logMessage);
            }
            for (int i = 0; i < 100; i++) {
                logMessage = LVLServer.createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockPhone", "mockSpecilization", Location.LVL.toString(), managerID);
                generateLog(logMessage);
            }
            System.out.println(LVLServer.getRecordCounts(managerID));
        }
    }

    static class MTLThread extends Client implements Runnable{
        @Override
        public void run() {
            setManagerID(Location.MTL.toString(),1);
            String logMessage;
            for (int i = 0; i < 100; i++) {
                logMessage = MTLServer.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), managerID);
                generateLog(logMessage);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 100; i++) {
                logMessage = MTLServer.createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockPhone", "mockSpecilization", Location.MTL.toString(), managerID);
                generateLog(logMessage);
            }
            System.out.println(MTLServer.getRecordCounts(managerID));
        }
    }

    static class DDOThread extends Client implements Runnable{
        @Override
        public void run() {
            setManagerID(Location.DDO.toString(),1);
            String logMessage;
            for (int i = 0; i < 100; i++) {
                logMessage = DDOServer.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), managerID);
                generateLog(logMessage);
            }
            for (int i = 0; i < 100; i++) {
                logMessage = DDOServer.createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockPhone", "mockSpecilization", Location.DDO.toString(), managerID);
                generateLog(logMessage);
            }
            System.out.println(DDOServer.getRecordCounts(managerID));
        }
    }
}
