import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.*;


public class TCPKeyValueStoreClient {

    private static final Logger logger = Logger.getLogger(TCPKeyValueStoreClient.class.getName());

    public static void main(String[] args) {

        setupLogger();

        if (args.length != 2) {
            logger.info("Usage: java TCPKeyValueStoreClient <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

                logger.info("\nWelcome to the TCP Key-Value Store Client");
                logger.info("\nPre-populated 5 key-value pairs:\n");
                String request;

                out.println("PUT name Diya");
                logger.info(in.readLine());
                out.println("PUT age 20");
                logger.info(in.readLine());
                out.println("PUT city Manama");
                logger.info(in.readLine());
                out.println("PUT country Bahrain");
                logger.info(in.readLine());
                out.println("PUT profession Student");
                logger.info(in.readLine());


                while (true) {

                // Display menu options
                logger.info("\nWhat would you like to do? \n1. Add a key-value pair\n2. Get a value by key\n3. Delete a key-value pair\n4. Exit\nEnter your choice:\n");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                

                switch (choice) {
                    case 1:
                        logger.info("\nEnter the key:");
                        String key = scanner.nextLine().toLowerCase();
                        logger.info("\nEnter the value:");
                        String value = scanner.nextLine();
                        request = "PUT " + key + " " + value;
                        break;

                    case 2:
                        logger.info("\nEnter the key:");
                        key = scanner.nextLine().toLowerCase();
                        request = "GET " + key;
                        break;

                    case 3:
                        logger.info("\nEnter the key:");
                        key = scanner.nextLine().toLowerCase();
                        request = "DELETE " + key;
                        break;

                    case 4:
                        logger.info("Exiting...");
                        request = "EXIT"; // Send exit command to the server
                        break;

                    default:
                        logger.info("Invalid choice. Please try again.");
                        continue; // Skip sending for invalid choices
                }

                // Send request to the server
                out.println(request);
                String response = in.readLine(); // Read response from the server
                logger.info(response);

                if (request.equalsIgnoreCase("EXIT")) {
                    break; // Exit the loop for exit command
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
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
            FileHandler fileHandler = new FileHandler("logs/TCPClient.log");
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
