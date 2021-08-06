package server;

import bean.Location;

public class MTLServer extends ServerImpl{
    public static void main(String[] args) {
        new ServerImpl().startServer(args, Location.MTL);
    }
}
