package server;

import ServerApp.Server;
import ServerApp.ServerHelper;
import factory.ConfigurationFactory;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;

public class Client {
    static Server serverImpl;
    public static void main(String args[]) {
        try {
            ORB orb = ORB.init(args, ConfigurationFactory.getConfiguration().getProperties());
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            NamingContextExt ncRef =
                    NamingContextExtHelper.narrow(objRef);
            serverImpl =
                    ServerHelper.narrow(ncRef.resolve_str("LVL"));
            System.out.println(serverImpl.getRecordCounts("LVL0001"));
        } catch (InvalidName | NotFound | org.omg.CosNaming.NamingContextPackage.InvalidName | CannotProceed e) {
            e.printStackTrace();
        }
    }
}