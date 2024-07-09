package Individual;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class LoginPayloadProvider {
    public static String getPasswordLoginPayload() throws IOException {
        // Read the file and return the content as a string
        File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\PreLogin\\PasswordLogin.json");
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }
    
    public static String getOtpPayload() throws IOException {
        // Read the file and return the content as a string
        File file = new File("C:\\Users\\Admin\\eclipse-workspace\\REST ASSURED GOAL\\src\\main\\java\\PreLogin\\OtpLogin.json");
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }
}