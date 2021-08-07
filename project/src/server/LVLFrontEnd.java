package server;

import bean.Location;

public class LVLFrontEnd extends ServerImpl{
    public static void main(String[] args) {
        new ServerImpl().startServer(args, Location.LVL);
    }
}
