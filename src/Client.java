import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;

public class Client {


    private String serverAddress;
    private String portNumber;
    private String type;
    private String id;
    private int maxAccess;
    private BufferedReader in;
    private PrintWriter out;

    public Client(String serverAddress, String portNumber, String id, int maxAccess, String type){
        this.serverAddress = serverAddress;
        this.portNumber = portNumber;
        this.id = id;
        this.maxAccess = maxAccess;
        this.type = type;
    }

    private void run() throws Exception{

        Socket socket = new Socket(serverAddress, Integer.parseInt(portNumber));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        PrintWriter log = new PrintWriter(new File("log" + id + ".txt"));
        log.write("Client Type: " + type + "\n");
        log.write("Client Name: " + id + "\n");
        log.write("rSeq\tsSeq\toVal\n");

        int accessCount = maxAccess;
        while(accessCount-- > 0){
            if(type.equalsIgnoreCase("Reader")){
                out.println(type + " " + id + " ");
            }else if(type.equalsIgnoreCase("Writer")){
                String value = String.valueOf(new Random().nextInt(100));
                out.println(type + " " + id + " " + value);
            }

            final String response = in.readLine();
            log.write(response + "\n");

            if(accessCount != 0){
                Thread.sleep(new Random().nextInt(10000));
            }
        }
        log.close();
    }

    public static void main(String[] args) throws Exception{
        Client client = new Client(args[0], args[1], args[2], Integer.parseInt(args[3]), args[4]);
        client.run();
    }

}
