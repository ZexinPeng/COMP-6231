package pers.zexin.client;

import pers.zexin.bean.Configuration;
import pers.zexin.server.CenterServer;

import java.rmi.Naming;

public class ManagerClient {
    private static Configuration configuration = new Configuration();

    public static void main(String[] args) {
        startClient();
    }

    public static void startClient() {
        try {
            String registryURL = "rmi://" + configuration.getHost() + ":" + configuration.getPort() + "/LVL";
            // find the remote object and cast it to an interface object
            CenterServer centerServer = (CenterServer) Naming.lookup(registryURL);
            System.out.println("Lookup completed " );
            // invoke the remote method
            String message = centerServer.getRecordCounts();
            System.out.println("HelloClient: " + message);
        } // end try
        catch (Exception e) {
            System.out.println("Exception in HelloClient: " + e);
        }
    }
}
