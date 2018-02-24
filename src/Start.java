import java.util.HashMap;

public class Start {

    private static final String configFile = "system.properties";

    public static void main(String[] args){
        Parser parser = new Parser();
        HashMap<String, String> properties = parser.parse(configFile);
    }
}
