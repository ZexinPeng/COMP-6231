package server;

import bean.Location;

public class MTLServer extends ServerImpl{
    public static void main(String[] args) {
        startServer(args, Location.MTL);
    }
}
