package server;

import bean.Location;

public class LVLServer extends ServerImpl{
    public static void main(String[] args) {
        startServer(args, Location.LVL);
    }
}
