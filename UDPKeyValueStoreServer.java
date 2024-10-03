import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.logging.*;

public class UDPKeyValueStoreServer {
    private static final int BUFFER_SIZE = 1024;
    private static Map<String, String> keyValueStore = new HashMap<>();
    private static final Logger logger = Logger.getLogger(UDPKeyValueStoreServer.class.getName());

    public static void main(String[] args) throws Exception {

        setupLogger();

        if (args.length != 1) {
            logger.info("Usage: java UDPKeyValueStoreServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);
        DatagramSocket socket = new DatagramSocket(port);
        byte[] buffer = new byte[BUFFER_SIZE];

        logger.info("UDP Server listening on port " + port);

        while (true) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            String receivedMessage = new String(request.getData(), 0, request.getLength());
            String responseMessage;

            // If the request is an EXIT command, break the loop and close the server
            if (receivedMessage.equalsIgnoreCase("EXIT") || receivedMessage.equalsIgnoreCase("")) {
                logger.info("Server is shutting down.");
                responseMessage = "Server is shutting down.";
                DatagramPacket response = new DatagramPacket(responseMessage.getBytes(), responseMessage.length(),
                        request.getAddress(), request.getPort());
                socket.send(response);
                break; // Exit the loop to shut down the server
            }

            responseMessage = processRequest(receivedMessage);
            DatagramPacket response = new DatagramPacket(responseMessage.getBytes(), responseMessage.length(),
                    request.getAddress(), request.getPort());
            socket.send(response);

            logger.info("Received and processed: " + receivedMessage + " from " + request.getAddress() + ":" + request.getPort());
        }

        socket.close();
    }

    private static String processRequest(String message) {
        String[] parts = message.split(" ");
        logger.info("Parts: " + Arrays.toString(parts)); // Use Arrays.toString to log parts correctly
        
        // Check for malformed requests;
        if (parts.length == 0) {
            logger.info("Received empty request from unknown address");
            return "Invalid request format";
        }

        String command = parts[0];
        String key = parts.length > 1 ? parts[1] : null;
        String value = parts.length > 2 ? parts[2] : null;

        switch (command.toUpperCase()) {
            case "PUT":
                if (key != null && value != null) {
                    if (keyValueStore.containsKey(key)) {
                        logger.info("Updated value for key: " + key);
                        keyValueStore.put(key, value);
                        return("Key already exists. Updating value for key: " + key);
                    }
                    else{
                        keyValueStore.put(key, value);
                        return "PUT successful: " + key + " = " + value;
                    }
                } else {
                    return "Invalid PUT format";
                }

            case "GET":
                return keyValueStore.containsKey(key) ? keyValueStore.get(key) : "Key not found";

            case "DELETE":
                return keyValueStore.remove(key) != null ? key + " DELETE successful" : "Key not found";

            default:
                return "Unknown command";
        }
    }

    private static void setupLogger() {
        try {
            Logger rootLogger = Logger.getLogger("");

            Handler[] handlers = rootLogger.getHandlers();
            for (Handler handler : handlers) {
                rootLogger.removeHandler(handler);
            }

            // Creating a file handler for logging to a file
            FileHandler fileHandler = new FileHandler("logs/UDPServer.log");
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
