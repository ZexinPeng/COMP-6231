package pers.zexin.server;

import java.rmi.Remote;

public interface Hello extends Remote {
    /**
     * This remote method returns a message.
     * param name - a String containing a name
     * returns a String message.
     */
    public String sayHello(String name) throws java.rmi.RemoteException;
}
