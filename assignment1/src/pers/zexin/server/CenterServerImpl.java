package pers.zexin.server;

import pers.zexin.bean.Configuration;
import pers.zexin.bean.Location;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CenterServerImpl implements CenterServer{
    ConcurrentHashMap<Character, List> recordMap = new ConcurrentHashMap<>();
    private static Configuration configuration = new Configuration();

    @Override
    public boolean createTRecord(String firstName, String lastName, String address, String phone, String specialization, Location location) {
        return false;
    }

    @Override
    public boolean createSRecord(String firstName, String lastName, String[] courseRegistered, String status, String statusDate) {
        return false;
    }

    @Override
    public String getRecordCounts() {
        return "123";
    }

    @Override
    public boolean editRecord(String recordID, String fieldName, String newValue) {
        return false;
    }

    public static int getPort() {
        return configuration.getPort();
    }

    public static void startServer(Location location) {
        try{
            CenterServer centerServer = new CenterServerImpl();
            CenterServer stub =
                    (CenterServer) UnicastRemoteObject.exportObject(centerServer, getPort());
            Registry registry = LocateRegistry.createRegistry(getPort());
            registry.bind(location.toString(), stub);
            System.out.println(location.toString() + " Server ready.");
        }
        catch (Exception re) {
            System.out.println(re);
        }
    }
}
