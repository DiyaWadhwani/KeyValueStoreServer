import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TCPKeyValueStoreClient {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java TCPKeyValueStoreClient <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        // Create a Scanner to read user inputs
        Scanner scanner = new Scanner(System.in);

        System.out.println("Connected to TCP Key-Value Store Server on port " + port);

        // Open a socket connection to the server
        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                // Display menu options
                System.out.println("What would you like to do? \n1. Add a key-value pair\n2. Get a value by key\n3. Delete a key-value pair\n4. Exit\nEnter your choice:");
                int choice = scanner.nextInt();
                scanner.nextLine();  // Consume the newline character left by nextInt()

                String request = "";

                switch (choice) {
                    case 1: 
                        System.out.println("Enter the key:");
                        String key = scanner.nextLine();
                        System.out.println("Enter the value:");
                        String value = scanner.nextLine();
                        request = "PUT " + key + " " + value;
                        break;
                    
                    case 2: 
                        System.out.println("Enter the key:");
                        key = scanner.nextLine();
                        request = "GET " + key;
                        break;
                    
                    case 3: 
                        System.out.println("Enter the key:");
                        key = scanner.nextLine();
                        request = "DELETE " + key;
                        break;

                    case 4:
                        System.out.println("Exiting...");
                        request = "EXIT"; // Send a specific command to exit the server
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                        continue;
                }

                // Send the user request to the server
                out.println(request);

                // If the exit command is sent, break the loop
                if (choice == 4) {
                    break;
                }

                // Receive and display the response from the server
                String response = in.readLine();
                log("Response: " + response);
            }
        } catch (IOException e) {
            log("Error: Could not connect to server or send request. Please check the server status and try again.");
        } finally {
            scanner.close();
        }
    }

    private static void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        System.out.println("[" + timestamp + "] " + message);
    }
}
