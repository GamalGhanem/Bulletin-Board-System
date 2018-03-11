import java.util.HashMap;

public class Start {

    private static final String configFile = "system.properties";

    public static void main(String[] args) {
        Parser parser = new Parser();
        HashMap<String, String> properties = parser.parse(configFile);

        start(properties);
    }

    private static void start(HashMap<String, String> map) {
        String[] parts = getNameAndIp(map.get("RW.server"));
        String serverUsername = parts[0].trim();
        String serverIp = parts[1].trim();

        int rmiPort = Integer.parseInt(map.get("RW.rmiregistry.port"));

        String serverPassword = map.get("RW.server.pwd");
        String numberOfAccesses = map.get("RW.numberOfAccesses");
        int readersNumber = Integer.parseInt(map.get("RW.numberOfReaders"));
        int writersNumber = Integer.parseInt(map.get("RW.numberOfWriters"));
        int whileCount = Integer.parseInt(numberOfAccesses) * (readersNumber + writersNumber);

        StringBuilder command = new StringBuilder();

        SSHHandler sshHandler = new SSHHandler();

        // start the server
        command.append("javac Server.java && java Server ");
        command.append(rmiPort);
        command.append(" ");
        command.append(whileCount);
        command.append(" ");
        command.append(serverIp);

        sshHandler.execCommand(serverUsername, serverIp, 22, serverPassword, command.toString());


        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // start the clients
        int clientId = 1;
        for (int i = 0; i < readersNumber; i++) {
            String[] clientParts = getNameAndIp(map.get("RW.reader" + i));
            String clientUserName = clientParts[0].trim();
            String clientIp = clientParts[1].trim();
            String clientPassword = map.get("RW.reader" + i + ".pwd");
            command = new StringBuilder();
            command.append("javac Client.java && java Client ");
            command.append(serverIp + " ");
            command.append(rmiPort + " ");
            command.append(clientId + " ");
            command.append(numberOfAccesses + " ");
            command.append("Reader");
            clientId++;
            sshHandler.execCommand(clientUserName, clientIp, 22, clientPassword, command.toString());
        }

        for (int i = 0; i < writersNumber; i++) {
            String[] clientParts = getNameAndIp(map.get("RW.writer" + i));
            String clientUserName = clientParts[0].trim();
            String clientIp = clientParts[1].trim();
            String clientPassword = map.get("RW.writer" + i + ".pwd");
            command = new StringBuilder();
            command.append("javac Client.java && java Client ");
            command.append(serverIp + " ");
            command.append(rmiPort + " ");
            command.append(clientId + " ");
            command.append(numberOfAccesses + " ");
            command.append("Writer");
            clientId++;
            sshHandler.execCommand(clientUserName, clientIp, 22, clientPassword, command.toString());
        }

    }

    private static String[] getNameAndIp(String str) {
        String[] parts = str.split("@");
        return parts;
    }
}
