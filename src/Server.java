import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {

    private static int clientNumber;
    private static AtomicInteger rSeq, sSeq, readersNumber;
    private static int portNumber, whileCount;
    private static ServerSocket socketListener;
    private static String value;
    private static Vector<String> readerLog;
    private static Vector<String> writerLog;

    public static void main(String[] args) throws Exception {

        System.out.println("The Server Starts Now...");

        clientNumber = 0;
        readersNumber = new AtomicInteger(0);
        rSeq = new AtomicInteger(1);
        sSeq = new AtomicInteger(1);
        readerLog = new Vector<>();
        writerLog = new Vector<>();
        readerLog.add("Readers");
        readerLog.add("sSeq\t\toVal\t\trID\t\trNum");
        writerLog.add("Writers");
        writerLog.add("sSeq\t\toVal\t\twID");

        portNumber = Integer.parseInt(args[0]);
        whileCount = Integer.parseInt(args[1]);
        value = "-1";

        ArrayList<Thread> threads = new ArrayList<>();

        try {
            socketListener = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            while (whileCount-- > 0) {
                Socket socket = socketListener.accept();
                Thread thread = new Thread(new RequestHandler(socket, clientNumber++));
                thread.start();
                threads.add(thread);
            }

            for (Thread thread : threads) {
                thread.join();
            }


        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            // print the file here
            try {
                PrintWriter log = new PrintWriter(new File("server.txt"));
                for (String line : readerLog) {
                    log.write(line + "\n");
                }
                for (String line : writerLog) {
                    log.write(line + "\n");
                }
                log.close();
                socketListener.close();

                System.out.println("The Server Ends Now...");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static class RequestHandler implements Runnable {

        private Socket socket;
        private int clientNumber;

        public RequestHandler(Socket socket, int clientNumber) {
            this.socket = socket;
            this.clientNumber = clientNumber;
            System.out.println("New connection with client# " + clientNumber + " on socket: " + socket);
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                String line = in.readLine();
                String[] parts = line.split(" ");
                Integer clientID = Integer.valueOf(parts[1]);

                synchronized (rSeq) {
                    int currentRseq = rSeq.getAndIncrement();
                    out.println(currentRseq);
                }

                Thread.sleep(new Random().nextInt(10000));

                if (parts[0].equalsIgnoreCase("Reader")) {
                    // write in the read vector

                    synchronized (readersNumber) {
                        int currentReader = readersNumber.incrementAndGet();
                        readerLog.add(sSeq + "\t\t" + value + "\t\t" + clientID + "\t\t" + currentReader);
                    }

                    // out.println(clientNumber + " " + current + value);
                } else if (parts[0].equalsIgnoreCase("Writer")) {

                    synchronized (value) {
                        value = parts[2];
                        writerLog.add(sSeq + "\t\t" + value + "\t\t" + clientID);
                    }
                }

                synchronized (sSeq) {
                    int currentSseq = sSeq.getAndIncrement();
                    out.println(currentSseq);
                    out.println(value);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    System.out.println("Couldn't close the socket");
                }
                System.out.println("Client# " + clientNumber + " connection now closed");
            }
        }
    }
}