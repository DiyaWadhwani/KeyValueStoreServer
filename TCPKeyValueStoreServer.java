import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class TCPKeyValueStoreServer {
    private static final Map<String, String> keyValueStore = new HashMap<>();

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java TCPKeyValueStoreServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("TCP Server listening on port " + port);
            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    handleClient(clientSocket);
                } catch (IOException e) {
                    System.err.println("Error handling client: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Could not start server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                String request;
                while ((request = in.readLine()) != null) {
                    // Check for EXIT command
                    if (request.equalsIgnoreCase("EXIT")) {
                        System.out.println("Client requested to exit. Shutting down the server.");
                        out.println("Server is shutting down.");
                        break; // Exit the loop, close the connection
                    }

                    String response = processRequest(request);
                    out.println(response);
                }
            } catch (IOException e) {
                System.err.println("Error in client connection: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }).start();
    }

    private static String processRequest(String request) {
        String[] parts = request.split(" ", 3);
        String command = parts[0].toUpperCase();
        String response;

        switch (command) {
            case "PUT":
                String key = parts[1];
                String value = parts[2];
                keyValueStore.put(key, value);
                System.out.println("Stored: " + key + " = " + value); // Debugging output
                response = "PUT successful: " + key + " = " + value;
                break;
            case "GET":
                key = parts[1];
                value = keyValueStore.get(key);
                System.out.println("Retrieving: " + key + " = " + value); // Debugging output
                response = value != null ? value : "Key not found";
                break;
            case "DELETE":
                key = parts[1];
                keyValueStore.remove(key);
                System.out.println("Deleted: " + key); // Debugging output
                response = "DELETE successful";
                break;
            default:
                response = "Invalid command";
        }
        return response;
    }
}
