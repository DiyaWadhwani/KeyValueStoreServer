import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class UDPKeyValueStoreClient {
    private static final int BUFFER_SIZE = 1024;
    private static final int TIMEOUT_MS = 2000; // Timeout for waiting for response

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java UDPKeyValueStoreClient <hostname> <port>");
            return;
        }

        String hostname = args[0];
        int port = Integer.parseInt(args[1]);
        DatagramSocket socket = new DatagramSocket();
        InetAddress address = InetAddress.getByName(hostname);
        Scanner scanner = new Scanner(System.in);
        socket.setSoTimeout(TIMEOUT_MS); // Set timeout for receiving response

        System.out.println("\nWelcome to the UDP Key-Value Store Client");
        System.out.println("\nPre-populated 5 key-value pairs:");

        // Send pre-populated key-value pairs to the server
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
            System.out.println("\nWhat would you like to do? \n1. Add a key-value pair\n2. Get a value by key\n3. Delete a key-value pair\n4. Exit\nEnter your choice:\n");
            int choice = scanner.nextInt();

            String request;

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
            log(response);
        } catch (SocketTimeoutException e) {
            System.out.println("No response from server within " + TIMEOUT_MS / 1000 + " seconds.");
        } catch (Exception e) {
            System.out.println("Error receiving response: " + e.getMessage());
        }
    }

    private static void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        System.out.println("\n[Response from server at: " + timestamp + "]\n" + message);
    }
}
