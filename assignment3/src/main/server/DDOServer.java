package main.server;

import main.bean.Location;
import main.factory.ConfigurationFactory;

import javax.xml.ws.Endpoint;

public class DDOServer extends ServerImpl {
    public static void main(String[] args) {
//        startServer(Location.DDO);
        startServerWithoutInitialData(Location.DDO);
        Endpoint.publish("http://localhost:"+ ConfigurationFactory.getConfiguration().getPortDDO() +"/DDOServer", new ServerImpl());
    }
}
