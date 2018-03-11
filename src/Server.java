import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Server extends UnicastRemoteObject implements RemoteHandle {

    private static int clientNumber;
    private static AtomicInteger rSeq, sSeq, readersNumber;
    private static int portNumber, whileCount;
    private static ServerSocket socketListener;
    private static String value, serverIp;
    private static BufferedWriter readerLog;
    private static BufferedWriter writerLog;

    protected Server() throws Exception {
        super();

        clientNumber = 0;
        readersNumber = new AtomicInteger(0);
        rSeq = new AtomicInteger(1);
        sSeq = new AtomicInteger(1);
        readerLog = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("Readers.txt"))));
        writerLog = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("Writers.txt"))));
        readerLog.write("Readers\n");
        readerLog.write("sSeq\t\toVal\t\trID\t\trNum\n");
        writerLog.write("Writers\n");
        writerLog.write("sSeq\t\toVal\t\twID\n");

        readerLog.flush();
        writerLog.flush();

        value = "-1";

    }

    public static void main(String[] args) throws Exception {

        System.out.println("The Server Starts Now...");

        portNumber = Integer.parseInt(args[0]);
        whileCount = Integer.parseInt(args[1]);
        serverIp = args[2];

        System.setProperty("java.rmi.server.hostname", serverIp);
        String name = "Board";
        Server server = new Server();

        Registry registry = LocateRegistry.createRegistry(portNumber);

        registry.rebind(name, server);

        System.out.println("Server now on port " + portNumber + "...");

    }

    @Override
    public synchronized String read(int id) throws RemoteException {

        int currentRseq = rSeq.getAndIncrement();
        int currentReader = readersNumber.incrementAndGet();
        try {
            readerLog.write(sSeq + "\t\t" + value + "\t\t" + id + "\t\t" + currentReader + "\n");
            readerLog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int currentSseq = sSeq.getAndIncrement();

        return new String(currentRseq + "," + currentSseq + "," + value);
    }

    @Override
    public synchronized String write(int value, int id) throws RemoteException {

        int currentRseq = rSeq.getAndIncrement();
        this.value = String.valueOf(value);
        try {
            writerLog.write(sSeq + "\t\t" + value + "\t\t" + id + "\n");
            writerLog.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int currentSseq = sSeq.getAndIncrement();
        return new String(currentRseq + "," + currentSseq + "," + value);
    }

}