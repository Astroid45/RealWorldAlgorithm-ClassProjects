import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Credential {
    public static String readApiKey() {
        String apiKey = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("api_key.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("api_key")) {
                    apiKey = line.split("=")[1].trim().replace("\"", ""); // Remove quotation marks
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading API key: " + e.getMessage());
        }
        return apiKey;
    }

    public static String readUsername() {
        String username = null;
        try (BufferedReader reader = new BufferedReader(new FileReader("api_key.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("USERNAME")) {
                    username = line.split("=")[1].trim().replace("\"", ""); // Remove quotation marks
                    break;
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading username: " + e.getMessage());
        }
        return username;
    }

}
