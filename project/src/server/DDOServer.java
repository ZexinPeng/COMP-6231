package server;

import bean.Location;

public class DDOServer extends ServerImpl{
    public static void main(String[] args) {
        new ServerImpl().startServer(args, Location.DDO);
    }
}
