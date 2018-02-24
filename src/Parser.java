import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Parser {

    public HashMap<String, String> parse(String path){
        Path filePath = Paths.get(path);
        Scanner scanner = null;
        try {
            scanner = new Scanner(filePath);
        }catch (IOException e){
            e.printStackTrace();
        }
        HashMap<String, String> properties = new HashMap<>();
        while (scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] parts = line.split("=");
            if(parts.length != 0){
                properties.put(parts[0].trim(), parts[1].trim());
            }
        }
        // debug the parser
        for (Map.Entry<String, String> entry: properties.entrySet()){
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
        return properties;
    }
}
