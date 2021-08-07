package server;

import bean.Location;

public class MTLFrontEnd extends ServerImpl{
    public static void main(String[] args) {
        new ServerImpl().startServer(args, Location.MTL);
    }
}
