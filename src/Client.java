import java.io.File;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;

public class Client {


    private static String serverAddress;
    private static int portNumber;
    private static String type;
    private static int id;
    private static int maxAccess;

    private static void startReader(RemoteHandle handle) throws Exception{
        PrintWriter log = new PrintWriter(new File("log" + id + ".txt"));
        log.write("Client Type: " + type + "\n");
        log.write("Client Name: " + id + "\n");
        log.write("rSeq\tsSeq\toVal\n");

        int accessCount = maxAccess;

        while (accessCount-- > 0) {

            String[] msg = handle.read(id).split(",");

            final String rSeq = msg[0];
            final String sSeq = msg[1];
            final String value = msg[2];

            log.write(rSeq + "\t" + sSeq + "\t" + value + "\n");

            if (accessCount != 0) {
                Thread.sleep(new Random().nextInt(10000));
            }

        }

        log.close();
    }

    private static void startWriter(RemoteHandle handle) throws Exception{
        PrintWriter log = new PrintWriter(new File("log" + id + ".txt"));
        log.write("Client Type: " + type + "\n");
        log.write("Client Name: " + id + "\n");
        log.write("rSeq\tsSeq\n");

        int accessCount = maxAccess;

        while (accessCount-- > 0) {

            int valueI = new Random().nextInt(10);
            String[] msg = handle.write(valueI, id).split(",");

            final String rSeq = msg[0];
            final String sSeq = msg[1];
            final String value = msg[2];

            log.write(rSeq + "\t" + sSeq + "\n");

            if (accessCount != 0) {
                Thread.sleep(new Random().nextInt(10000));
            }

        }

        log.close();
    }

    public static void main(String[] args) throws Exception {
        serverAddress = args[0];
        portNumber = Integer.parseInt(args[1]);
        id = Integer.parseInt(args[2]);
        maxAccess = Integer.parseInt(args[3]);
        type = args[4];

        System.setProperty("java.rmi.server.hostname", serverAddress);

        String name = "Board";
        Registry registry = LocateRegistry.getRegistry(portNumber);
        RemoteHandle handle = (RemoteHandle) registry.lookup(name);

        System.out.println("Welcome Client No. " + id);

        if(type.equalsIgnoreCase("Reader")){
            startReader(handle);
        } else if(type.equalsIgnoreCase("Writer")){
            startWriter(handle);
        }

        System.out.println("Client No. " + id + " Closed ..." );

    }

}