package main.server;

import main.bean.Location;
import main.factory.ConfigurationFactory;

import javax.xml.ws.Endpoint;

public class MTLServer extends ServerImpl {
    public static void main(String[] args) {
        startServer(Location.MTL);
        Endpoint.publish("http://localhost:"+ ConfigurationFactory.getConfiguration().getPortMTL() +"/MTLServer", new ServerImpl());
    }
}
