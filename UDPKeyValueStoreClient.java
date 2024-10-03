import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;
import java.util.logging.*;

public class UDPKeyValueStoreClient {
    private static final int BUFFER_SIZE = 1024;
    private static final int TIMEOUT_MS = 2000; // Timeout for waiting for response
    private static final Logger logger = Logger.getLogger(UDPKeyValueStoreClient.class.getName());

    public static void main(String[] args) throws Exception {

        //counter for put, get and delete operations
        int putCount = 0;
        int getCount = 0;
        int deleteCount = 0;
        
        //setting up logger
        setupLogger();

        //checking if the hostname and port number are provided
        if (args.length != 2) {
            logger.info("Usage: java UDPKeyValueStoreClient <hostname> <port>");
            return;
        }

        //retrieving the hostname and port number
        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(hostname);

        Scanner scanner = new Scanner(System.in);
        socket.setSoTimeout(TIMEOUT_MS); // Setting timeout for receiving response

        logger.info("\nWelcome to the UDP Key-Value Store Client");
        logger.info("\nPre-populated 5 key-value pairs:");

        // Sending pre-populated key-value pairs to the server
        String[] prePopulatedRequests = {
            "PUT name Diya",
            "PUT age 20",
            "PUT city Manama",
            "PUT country Bahrain",
            "PUT profession Student"
        };
        for (String request : prePopulatedRequests) {
            sendRequest(socket, address, port, request);
            receiveResponse(socket);
        }

        while (true) {
            // Display menu options
            logger.info("\nWhat would you like to do? \n1. Add a key-value pair\n2. Get a value by key\n3. Delete a key-value pair\n4. Exit\nEnter your choice:\n");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String request="";

            switch (choice) {
                case 1:
                    logger.info("\nEnter the key (in lowercase):");
                    String key = scanner.nextLine().toLowerCase();
                    logger.info("\nEnter the value (use underscore between multi-word values):");
                    String value = scanner.nextLine();
                    request = "PUT " + key + " " + value;
                    putCount++;
                    break;

                case 2:
                    logger.info("\nEnter the key (in lowercase):");
                    key = scanner.nextLine().toLowerCase();
                    request = "GET " + key;
                    getCount++;
                    break;

                case 3:
                    logger.info("\nEnter the key (in lowercase):");
                    key = scanner.nextLine().toLowerCase();
                    request = "DELETE " + key;
                    deleteCount++;
                    break;

                case 4:
                    if(putCount > 4 && getCount > 4 && deleteCount > 4){
                        logger.info("Exiting...");
                        request = "EXIT"; // Send exit command to the server
                    }
                    else if(putCount < 5 || getCount < 5 || deleteCount < 5){
                        logger.info("You need to perform "+(5-putCount)+" more PUT, "+(5-getCount)+" GET, and "+(5-deleteCount)+" DELETE operations before exiting");
                        continue;
                    }
                    break;

                default:
                    logger.info("Invalid choice. Please try again.");
                    continue; // Skip the sending process for invalid choices
            }

            // Send request to the server
            sendRequest(socket, address, port, request);

            // Handle response from the server
            receiveResponse(socket);

            if (request.equalsIgnoreCase("EXIT")) {
                break; // Exit the loop for exit command
            }
        }

        socket.close();
        scanner.close();
    }

    private static void sendRequest(DatagramSocket socket, InetAddress address, int port, String request) throws Exception {
        DatagramPacket packet = new DatagramPacket(request.getBytes(), request.length(), address, port);
        socket.send(packet);
    }

    private static void receiveResponse(DatagramSocket socket) {
        try {
            byte[] buffer = new byte[BUFFER_SIZE];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.receive(responsePacket);
            String response = new String(responsePacket.getData(), 0, responsePacket.getLength());
            logger.info(response);
        } catch (SocketTimeoutException e) {
            logger.info("No response from server within " + TIMEOUT_MS / 1000 + " seconds.");
        } catch (Exception e) {
            logger.info("Error receiving response: " + e.getMessage());
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
            FileHandler fileHandler = new FileHandler("logs/UDPClient.log");
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
