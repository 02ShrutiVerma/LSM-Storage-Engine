import java.io.*;
import java.net.*;

public class DatabaseClient {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 8080);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // A. Test SAVE
            out.println("PUT SecretKey:TopSecretValue123");
            System.out.println("Step A (Save): " + in.readLine());

            // B. Test DELETE
            out.println("DELETE SecretKey");
            System.out.println("Step B (Delete): " + in.readLine());

            // C. Test VERIFY
            out.println("GET SecretKey");
            System.out.println("Step C (Verify): " + in.readLine());

        } catch (IOException e) {
            System.err.println("Client Error: " + e.getMessage());
        }
    }
}
