package main.server;

import javax.xml.ws.Endpoint;

import main.bean.Location;
import main.factory.ConfigurationFactory;

public class LVLServer extends ServerImpl {
    public static void main(String[] args) {
        startServer(args, Location.LVL);
        Endpoint.publish("http://localhost:"+ ConfigurationFactory.getConfiguration().getPortLVL() +"/LVLServer", new ServerImpl());
    }
}
