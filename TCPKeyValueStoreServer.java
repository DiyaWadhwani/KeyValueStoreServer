import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

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
                        log("Client requested to exit. Shutting down the connection.");
                        out.println("Goodbye! Closing connection.");
                        out.flush();
                        break; // Exit the loop, close the connection
                    }

                    String response = processRequest(request);
                    out.println(response);
                    out.flush(); // Flush the output to ensure immediate delivery
                    log("Processed request: " + request);
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
        String[] parts = request.split(" ");
        log("Parts: " + Arrays.toString(parts)); // Use Arrays.toString to log parts correctly
        
        // Check for malformed requests; LIST does not require a key or value
        if (parts.length == 0) {
            log("Received empty request from unknown address");
            return "Invalid request format";
        }

        String command = parts[0].toUpperCase();
        String key = parts.length > 1 ? parts[1] : null;
        String value;
        String response;

        switch (command) {
            case "PUT":
                value = parts.length > 2 ? parts[2] : null;
                if (key != null && value != null) {
                    if (keyValueStore.containsKey(key)) {
                        System.out.println("Updated value for key: " + key);
                        keyValueStore.put(key, value);
                        response = "Key already exists. Updating value for key: " + key;
                    }
                    else{
                        keyValueStore.put(key, value);
                        response = "PUT successful: " + key + " = " + value;
                    }
                } else {
                    response = "Invalid PUT format. Use: PUT <key> <value>";
                }
                break;

            case "GET":
                value = keyValueStore.get(key);
                response = value != null ? value : "Key not found: " + key;
                break;

            case "DELETE":
                if (keyValueStore.remove(key) != null) {
                    response = "DELETE successful: " + key;
                } else {
                    response = "Key not found for deletion: " + key;
                }
                break;

            default:
                response = "Invalid command. Available commands: PUT, GET, DELETE, EXIT.";
                break;
        }
        
        return response;
    }

    private static void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        System.out.println("[" + timestamp + "] " + message);
    }
}
