package server;

import bean.Location;

public class DDOFrontEnd extends ServerImpl{
    public static void main(String[] args) {
        new ServerImpl().startServer(args, Location.DDO);
    }
}
