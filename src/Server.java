import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Server implements Runnable {

    private String address;
    private int portNumber;
    private int numAccess;
    private ServerSocket socket;
    private AtomicInteger sequenceNumber;
    private int value;
    private Vector<String> readerLog;
    private Vector<String> writerLog;

    public Server(String address, int portNumber, int numAccess){
        this.address = address;
        this.portNumber = portNumber;
        this.numAccess = numAccess;
        this.sequenceNumber = new AtomicInteger(1);
        this.readerLog = new Vector<>();
        this.writerLog = new Vector<>();
        this.value = -1;

        try{
            socket = new ServerSocket(portNumber);
        }catch (IOException e){
            e.getMessage();
        }
    }


    @Override
    public void run() {

    }
}
