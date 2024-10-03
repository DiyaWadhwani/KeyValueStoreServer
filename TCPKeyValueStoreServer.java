import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.logging.*;

public class TCPKeyValueStoreServer {
    private static final Map<String, String> keyValueStore = new HashMap<>();
    private static final Logger logger = Logger.getLogger(TCPKeyValueStoreServer.class.getName());

    public static void main(String[] args) {

        setupLogger();

        if (args.length != 1) {
            logger.info("Usage: java TCPKeyValueStoreServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("TCP Server listening on port " + port);
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
                    if (request.equalsIgnoreCase("EXIT") || request.equalsIgnoreCase("")) {
                        logger.info("Client requested to exit. Shutting down the connection.");
                        out.println("Goodbye! Closing connection.");
                        out.flush();
                        break; // Exit the loop, close the connection
                    }

                    String response = processRequest(request);
                    out.println(response);
                    out.flush(); // Flush the output to ensure immediate delivery
                    logger.info("Processed request: " + request);
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
        logger.info("Parts: " + Arrays.toString(parts)); // Use Arrays.toString to log parts correctly
        
        // Check for malformed requests;
        if (parts.length == 0) {
            logger.info("Received empty request from unknown address");
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
                        logger.info("Updated value for key: " + key);
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

    private static void setupLogger() {
        try {
            Logger rootLogger = Logger.getLogger("");

            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // Creating a file handler for logging to a file
            FileHandler fileHandler = new FileHandler("logs/TCPServer.log");
            fileHandler.setFormatter(new CustomFormatter());
            logger.addHandler(fileHandler);

            // Creating a console handler
            ConsoleHandler consoleHandler = new ConsoleHandler();
            consoleHandler.setFormatter(new CustomFormatter());
            logger.addHandler(consoleHandler);

            // Setting logger level
            logger.setLevel(Level.INFO);

        } catch (IOException e) {
            System.err.println("Error setting up logger: " + e.getMessage());
        }
    }
}
