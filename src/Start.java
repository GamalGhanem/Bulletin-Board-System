import java.util.HashMap;

public class Start {

    private static final String configFile = "system.properties";

    public static void main(String[] args){
        Parser parser = new Parser();
        HashMap<String, String> properties = parser.parse(configFile);

        start(properties);
    }

    private static void start(HashMap<String, String> map){
        String server = map.get("RW.server");
        int serverPort = Integer.parseInt(map.get("RW.server.port"));
        String serverPassword = map.get("RW.server.password");
        String numberOfAccesses = map.get("RW.numberOfAccesses");
        int readersNumber = Integer.parseInt(map.get("RW.numberOfReaders"));
        int writersNumber = Integer.parseInt(map.get("RW.numberOfWriters"));


        StringBuilder command = new StringBuilder();

        SSHHandler sshHandler = new SSHHandler();

        // start the server
        command.append("javac Server.java && java Server ");
        command.append(serverPort);

        sshHandler.execCommand("server", server, serverPort, serverPassword, command.toString());


        try{
            Thread.sleep(3000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        // start the clients
        int clientId = 1;
        for(int i = 0; i < readersNumber; i++){
            String clientUserName = map.get("RW.reader" + i);
            String clientPassword = map.get("RW.reader" + i + ".password");
            String clientIp = map.get("RW.reader"+ i + ".ip");
            command = new StringBuilder();
            command.append("javac Client.java && java Client ");
            command.append(server + " ");
            command.append(serverPort + " ");
            command.append(clientId + " ");
            command.append(numberOfAccesses + " ");
            command.append("Reader");
            clientId++;
            sshHandler.execCommand(clientUserName, clientIp, serverPort, clientPassword, command.toString());
        }

        for(int i = 0; i < writersNumber; i++){
            String clientUserName = map.get("RW.writer" + i);
            String clientPassword = map.get("RW.writer" + i + ".password");
            String clientIp = map.get("RW.writer"+ i + ".ip");
            command = new StringBuilder();
            command.append("javac Client.java && java Client ");
            command.append(server + " ");
            command.append(serverPort + " ");
            command.append(clientId + " ");
            command.append(numberOfAccesses + " ");
            command.append("Writer");
            clientId++;
            sshHandler.execCommand(clientUserName, clientIp, serverPort, clientPassword, command.toString());
        }

    }
}
