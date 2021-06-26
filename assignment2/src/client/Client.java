package client;

import ServerApp.Server;
import ServerApp.ServerHelper;
import bean.Location;
import factory.ConfigurationFactory;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import util.Tool;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    protected Server serverImpl;
    protected String managerID;

    public static void main(String[] args) {
        Client client = new Client();
        client.initialClient(args, Location.LVL.toString(), 1);
    }

    private void run(String[] args) {
        initialClient(args, Location.LVL.toString(), 1);
        String logMessage = serverImpl.transferRecord(managerID, "SR00001", Location.DDO.toString());
        generateLog(logMessage);

        initialClient(args, Location.DDO.toString(), 1);
        logMessage = serverImpl.transferRecord(managerID, "SR00001", Location.LVL.toString());
        generateLog(logMessage);
    }

    protected void initialClient(String[] args, String location, int clientID) {
        try {
            ORB orb = ORB.init(args, ConfigurationFactory.getConfiguration().getProperties());
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            serverImpl =  ServerHelper.narrow(ncRef.resolve_str(location));
        } catch (NotFound | org.omg.CosNaming.NamingContextPackage.InvalidName | CannotProceed | InvalidName e) {
            e.printStackTrace();
        }
        setManagerID(location,clientID);
    }

    private static void generateLog(String status, String managerID, String operationaMessage) {
        String message;
        if (status.equals("[ERROR]")) {
            message = status + " something goes wrong in the server.";
        }
        else {
            message = status + " date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
                    + ", managerID: " + managerID + ", operation: " + operationaMessage;
        }
        System.out.println(message);
        Tool.write2LogFile(message, ConfigurationFactory.getConfiguration().getClientLogDirectory(), managerID);
    }

    protected void generateLog(String message) {
        System.out.println(message);
        Tool.write2LogFile(message, ConfigurationFactory.getConfiguration().getClientLogDirectory(), managerID);
    }

    private void setManagerID(String location, int num) {
        StringBuilder sb = new StringBuilder(location);
        for (int i = 0; i < 4 - String.valueOf(num).length(); i++) {
            sb.append("0");
        }
        managerID = sb.append(num).toString();
    }
}
