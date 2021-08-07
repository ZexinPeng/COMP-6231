package client;

import bean.Location;
import util.Tool;

public class TestScenario3 {
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
            initialClient(new String[]{}, Location.LVL.toString(), 1);
            String logMessage;
            for (int i = 0; i < 100; i++) {
                logMessage = serverImpl.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), managerID);
                generateLog(logMessage);
            }
            for (int i = 0; i < 100; i++) {
                logMessage = serverImpl.createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockPhone", "mockSpecilization", Location.LVL.toString(), managerID);
                generateLog(logMessage);
            }
            System.out.println(serverImpl.getRecordCounts(managerID));
        }
    }

    static class MTLThread extends Client implements Runnable{
        @Override
        public void run() {
            initialClient(new String[]{}, Location.MTL.toString(), 1);
            String logMessage;
            for (int i = 0; i < 100; i++) {
                logMessage = serverImpl.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), managerID);
                generateLog(logMessage);
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < 100; i++) {
                logMessage = serverImpl.createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockPhone", "mockSpecilization", Location.MTL.toString(), managerID);
                generateLog(logMessage);
            }
            System.out.println(serverImpl.getRecordCounts(managerID));
        }
    }

    static class DDOThread extends Client implements Runnable{
        @Override
        public void run() {
            initialClient(new String[]{}, Location.DDO.toString(), 1);
            String logMessage;
            for (int i = 0; i < 100; i++) {
                logMessage = serverImpl.createSRecord("mockFirstName", "mockLastName", "mockCourse1,mockCourse2", "active", Tool.getCurrentTime(), managerID);
                generateLog(logMessage);
            }
            for (int i = 0; i < 100; i++) {
                logMessage = serverImpl.createTRecord("mockFirstName", "mockLastName", "mockAddress", "mockPhone", "mockSpecilization", Location.DDO.toString(), managerID);
                generateLog(logMessage);
            }
            System.out.println(serverImpl.getRecordCounts(managerID));
        }
    }
}
