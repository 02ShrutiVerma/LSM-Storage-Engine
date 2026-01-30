import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DatabaseServer {
    private final MemTable db = new MemTable();
    private final int PORT = 8080;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println(">>> [MULTI-THREADED SERVER LIVE] Listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println(">>> [NEW CLIENT CONNECTED]: " + clientSocket.getInetAddress());
                threadPool.execute(() -> handleClientRequest(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }

    private void handleClientRequest(Socket clientSocket) {
        try (Socket socket = clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request;
            // KEEP THE CONNECTION OPEN for multiple commands
            while ((request = in.readLine()) != null) {
                if (request.trim().isEmpty()) continue;

                System.out.println(">>> [SERVER] Executing: " + request);

                try {
                    String command = request.trim();
                    if (command.startsWith("PUT ")) {
                        String data = command.substring(4).trim();
                        String[] parts = data.split(":", 2);
                        if (parts.length == 2) {
                            db.put(parts[0], parts[1]);
                            out.println("OK: Saved " + parts[0]);
                        }
                    } else if (command.startsWith("DELETE ")) {
                        String key = command.substring(7).trim();
                        db.delete(key);
                        out.println("OK: Deleted " + key);
                    } else if (command.startsWith("GET ")) {
                        String key = command.substring(4).trim();
                        String val = db.get(key);
                        out.println(val != null ? val : "NOT_FOUND");
                    }
                    out.flush(); // Ensure the client gets the message
                } catch (Exception e) {
                    out.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println(">>> [NETWORK ERROR]: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new DatabaseServer().start();
    }
}
