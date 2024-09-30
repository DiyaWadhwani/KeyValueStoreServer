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

        while (true) {
            // Display menu options
            System.out.println("What would you like to do? \n1. Add a key-value pair\n2. Get a value by key\n3. Delete a key-value pair\n4. Exit\nEnter your choice:");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

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
                    continue;
            }

            // Send the request to the server
            byte[] buffer = request.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
            socket.send(packet);

            // If the exit command is sent, break the loop
            if (choice == 4) {
                // Wait for the server's exit response
                packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
                try {
                    socket.receive(packet);
                    String response = new String(packet.getData(), 0, packet.getLength());
                    log("Response: " + response);
                } catch (SocketTimeoutException e) {
                    log("Error: No response received for exit command.");
                }
                break; // Exit the loop after receiving the exit confirmation
            }

            // Receive and display the response from the server
            packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
            try {
                socket.receive(packet);
                String response = new String(packet.getData(), 0, packet.getLength());
                log("Response: " + response);
            } catch (SocketTimeoutException e) {
                log("Error: No response received for request: " + request);
            } catch (Exception e) {
                log("Error: Received malformed response.");
            }
        }

        socket.close();
        scanner.close();
    }

    private static void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        System.out.println("[" + timestamp + "] " + message);
    }
}
