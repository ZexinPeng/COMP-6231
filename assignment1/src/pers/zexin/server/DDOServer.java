package pers.zexin.server;

import pers.zexin.bean.Location;

public class DDOServer extends CenterServerImpl{
    public static void main(String[] args) {
        startServer(Location.DDO);
    }
}
