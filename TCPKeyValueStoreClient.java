import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class TCPKeyValueStoreClient {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TCPKeyValueStoreClient <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(hostname, port);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             Scanner scanner = new Scanner(System.in)) {

                System.out.println("\nWelcome to the TCP Key-Value Store Client");
                System.out.println("\nPre-populated 5 key-value pairs:\n");
                String request;

                out.println("PUT name Diya");
                log(in.readLine());
                out.println("PUT age 20");
                log(in.readLine());
                out.println("PUT city Manama");
                log(in.readLine());
                out.println("PUT country Bahrain");
                log(in.readLine());
                out.println("PUT profession Student");
                log(in.readLine());


                while (true) {

                // Display menu options
                System.out.println("\nWhat would you like to do? \n1. Add a key-value pair\n2. Get a value by key\n3. Delete a key-value pair\n4. Exit\nEnter your choice:\n");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume the newline character

                

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
                        request = "EXIT"; // Send exit command to the server
                        break;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                        continue; // Skip sending for invalid choices
                }

                // Send request to the server
                out.println(request);
                String response = in.readLine(); // Read response from the server
                log(response);

                if (request.equalsIgnoreCase("EXIT")) {
                    break; // Exit the loop for exit command
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        System.out.println("\n[Response from server at: " + timestamp + "]:\n" + message);
    }
}
