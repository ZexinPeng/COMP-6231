package server;

import ServerApp.Server;
import ServerApp.ServerHelper;
import ServerApp.ServerPOA;
import bean.Location;
import bean.Record;
import bean.StudentRecord;
import bean.TeacherRecord;
import factory.ConfigurationFactory;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import util.Configuration;
import util.Tool;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ServerImpl extends ServerPOA {
    private static HashMap<Character, List<Record>> recordMap = new HashMap<>();
    final private static Configuration configuration = ConfigurationFactory.getConfiguration();
    private static Location location;
    private static int teacherRecordNum = 0;
    private static int studentRecordNum = 0;

    protected static void startServer(String[] args, Location locationPara) {
        if (locationPara == null) {
            Tool.printError("the location of the server should be indicated!");
        }
        location = locationPara;
        startCountThread();
        initiate();
        try {
            ORB orb = ORB.init(args, configuration.getProperties());
            // Portable Object Adapter (POA)
            // get reference to rootpoa & activate the POAManager
            POA rootpoa = (POA) orb.resolve_initial_references("RootPOA");
            rootpoa.the_POAManager().activate();
            // create servant and register it with the ORB
            ServerImpl serverImpl = new ServerImpl();
            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(serverImpl);
            // and cast the reference to a CORBA reference
            Server href = ServerHelper.narrow(ref);
            // get the root naming context
            // NameService invokes the transient name service
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            // Use NamingContextExt, which is part of the
            // Interoperable Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            // bind the Object Reference in Naming
            String name = location.toString();
            NameComponent path[] = ncRef.to_name(name);
            ncRef.rebind(path, href);
            System.out.println(location.toString() + " Server is ready.");
            // wait for invocations from clients
            orb.run();
        } catch (WrongPolicy wrongPolicy) {
            wrongPolicy.printStackTrace();
        } catch (AdapterInactive adapterInactive) {
            adapterInactive.printStackTrace();
        } catch (ServantNotActive servantNotActive) {
            servantNotActive.printStackTrace();
        } catch (InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (CannotProceed cannotProceed) {
            cannotProceed.printStackTrace();
        } catch (org.omg.CosNaming.NamingContextPackage.InvalidName invalidName) {
            invalidName.printStackTrace();
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }
    }

    @Override
    public String createTRecord(String firstName, String lastName, String address, String phone, String specialization, String location, String managerID) {
        return "123";
    }

    @Override
    public String createSRecord(String firstName, String lastName, String courseRegistered, String status, String statusDate, String managerID) {
        return "123";
    }

    @Override
    public String getRecordCounts(String managerID) {
        return "123";
    }

    @Override
    public String editRecord(String recordID, String fieldName, String newValue, String managerID) {
        return "123";
    }

    @Override
    public String transferRecord(String managerID, String recordID, String remoteCenterServerName) {
        return "123";
    }

    /**
     * start a new thread to listen
     */
    private static void startCountThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                int port;
                if (location.equals(Location.LVL)) {
                    port = configuration.getPortLVL();
                } else if (location.equals(Location.DDO)) {
                    port = configuration.getPortDDO();
                } else {
                    port = configuration.getPortMTL();
                }
                DatagramSocket aSocket = null;
                try{
                    aSocket = new DatagramSocket(port);
                    // create socket at agreed port
                    byte[] buffer = new byte[1000];
                    while(true){
                        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                        aSocket.receive(request);
                        DatagramPacket reply = new DatagramPacket(Tool.int2ByteArray(teacherRecordNum + studentRecordNum), 4,
                                request.getAddress(), request.getPort());
                        aSocket.send(reply);
                    }
                }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
                }catch (IOException e) {System.out.println("IO: " + e.getMessage());
                }finally {if(aSocket != null) aSocket.close();}
            }
        }).start();
    }

    /**
     * insert some records at the beginning
     */
    private static void initiate() {
        List<Record> recordList = new LinkedList<>();
        if (location.toString().equals("LVL")) {
            recordList.add(new TeacherRecord("TR00000", "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new TeacherRecord("TR00001", "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new StudentRecord("SR00002", "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
        } else if (location.toString().equals("MTL")) {
            recordList.add(new TeacherRecord("TR00003", "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new StudentRecord("SR00004", "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
        } else {
            recordList.add(new TeacherRecord("TR00005", "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new StudentRecord("SR00006", "mockFirstName", "mockLastName", new String[]{"mockCourse"}, "active", Tool.getCurrentTime()));
            recordList.add(new TeacherRecord("TR00007", "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
            recordList.add(new TeacherRecord("TR00008", "mockFirstName", "mockLastName", "mockAddress", "mockNumber", "mockSpecialization", location));
        }
        insertRecords(recordList);
        System.out.println("the initial number of records is " + (teacherRecordNum + studentRecordNum));
    }

    /**
     * insert recordsList into the server
     * @param records
     */
    private static void insertRecords(List<Record> records) {
        for (Record record: records) {
            List<Record> recordList = recordMap.get(record.getLastName().charAt(0));
            if (recordList == null) {
                recordList = new LinkedList();
                recordMap.put(record.getLastName().charAt(0), recordList);
            }
            if (record instanceof TeacherRecord) {
                TeacherRecord teacherRecord = new TeacherRecord(record.getRecordID(), record.getFirstName(), record.getLastName()
                        , ((TeacherRecord) record).getAddress(), ((TeacherRecord) record).getPhone(), ((TeacherRecord) record).getSpecialization(), location);
                recordList.add(teacherRecord);
                teacherRecordNum++;
            } else if (record instanceof StudentRecord){
                StudentRecord studentRecord = new StudentRecord(record.getRecordID(), record.getFirstName(), record.getLastName()
                        , ((StudentRecord) record).getCoursesRegistered(), ((StudentRecord) record).getStatus(), ((StudentRecord) record).getStatusDate());
                recordList.add(studentRecord);
                studentRecordNum++;
            }
        }
    }
}
