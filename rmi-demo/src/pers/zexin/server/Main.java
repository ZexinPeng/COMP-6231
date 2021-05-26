package pers.zexin.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Main {
    public static void main(String[] args) {
        InputStreamReader is = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(is);
        String portNum, registryURL;
        try{
            System.out.println("Enter the RMIregistry port number:");
            portNum = (br.readLine()).trim();
            int RMIPortNum = Integer.parseInt(portNum);
            Hello hello = new HelloImpl();
            Hello stub =
                    (Hello) UnicastRemoteObject.exportObject(hello, RMIPortNum);
            Registry registry = LocateRegistry.createRegistry(RMIPortNum);
            registry.bind("hello", stub);
            System.out.println("Hello Server ready.");
        }// end try
        catch (Exception re) {
            System.out.println("Exception in HelloServer.main: " + re);
        }
    }
}
