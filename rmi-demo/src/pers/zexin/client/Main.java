package pers.zexin.client;

import pers.zexin.server.Hello;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.rmi.Naming;

public class Main {
    public static void main(String[] args) {
        try {
            int RMIPort;
            String hostName;
            InputStreamReader is = new InputStreamReader(System.in);
            BufferedReader br = new BufferedReader(is);
            System.out.println("Enter the RMIRegistry host name:");
            hostName = br.readLine();
            System.out.println("Enter the RMIregistry port number:");
            String portNum = br.readLine();
            String registryURL = "rmi://" + hostName+ ":" + portNum + "/hello";
            System.out.println(registryURL);
            // find the remote object and cast it to an interface object
            Hello h = (Hello) Naming.lookup(registryURL);
            System.out.println("Lookup completed " );
            // invoke the remote method
            String message = h.sayHello("Donald Duck");
            System.out.println("HelloClient: " + message);
        } // end try
        catch (Exception e) {
            System.out.println("Exception in HelloClient: " + e);
        }
    }
}
