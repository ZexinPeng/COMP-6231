package server;

import bean.Location;

public class DDOServer extends ServerImpl{
    public static void main(String[] args) {
        startServer(args, Location.DDO);
    }
}
