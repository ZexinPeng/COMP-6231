package server;

import bean.Location;

public class LVLServer extends ServerImpl{
    public static void main(String[] args) {
        new ServerImpl().startServer(args, Location.LVL);
    }
}
