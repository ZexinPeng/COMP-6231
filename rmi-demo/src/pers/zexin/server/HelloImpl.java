package pers.zexin.server;

import java.rmi.RemoteException;

public class HelloImpl implements Hello{
    @Override
    public String sayHello(String name) throws RemoteException {
        return "hello " + name;
    }
}
