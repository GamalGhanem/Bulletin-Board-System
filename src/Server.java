import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private static int clientNumber;
    private static AtomicInteger sequenceNumber;
    private static int readersNumber;
    private static int portNumber;
    private static ServerSocket socketListener;
    private static String value;
    private static Vector<String> readerLog;
    private static Vector<String> writerLog;

    public static void main(String[] args) throws Exception {
        if(args == null || args.length != 1){
            System.err.println("Missing Args");
            return;
        }

        System.out.println("The Server Starts Now...");

        clientNumber = 0;
        readersNumber = 0;
        sequenceNumber = new AtomicInteger(1);
        readerLog = new Vector<>();
        writerLog = new Vector<>();
        readerLog.add("Readers");
        readerLog.add("sSeq\t\toVal\t\trID\t\trNum");
        writerLog.add("Writers");
        writerLog.add("sSeq\t\toVal\t\twID");

        portNumber = Integer.parseInt(args[0]);
        value = "-1";

        try{
            socketListener = new ServerSocket(portNumber);
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

        try{
            while(true){
                Socket socket = socketListener.accept();
                new RequestHandler(socket, clientNumber++).start();
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        } finally {
            // print the file here
            try{
                PrintWriter log = new PrintWriter(new File("server.txt"));
                for(String line: readerLog){
                    log.write(line + "\n");
                }
                for(String line: writerLog){
                    log.write(line + "\n");
                }
                log.close();
                socketListener.close();
            }catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static class RequestHandler extends Thread{

        private Socket socket;
        private int clientNumber;

        public RequestHandler(Socket socket, int clientNumber){
            this.socket = socket;
            this.clientNumber = clientNumber;
            System.out.println("New connection with client# " + clientNumber + "on socket: " + socket);
        }


        public void run() {
            try{
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String line = in.readLine();
                String[] parts = line.split(" ");
                Integer clientID = Integer.valueOf(parts[1]);
                if(parts[0].equalsIgnoreCase("Reader")){
                    // write in the read vector
                    readersNumber++;
                    int current = sequenceNumber.getAndIncrement();
                    readerLog.add(current + "\t\t" + value + "\t\t" + clientID + "\t\t" + readersNumber);
                    out.println(clientNumber + " " + current + value);
                }else if(parts[0].equalsIgnoreCase("Writer")){
                    value = parts[2];
                    int current = sequenceNumber.getAndIncrement();
                    writerLog.add(current + "\t\t" + value + "\t\t" + clientID);
                    out.println(clientNumber + " " + current);
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            } finally {
                try{
                    socket.close();
                } catch (IOException e){
                    System.out.println("Couldn't close the socket");
                }
                System.out.println("Client# " + clientNumber + "connection now closed");
            }
        }
    }
}