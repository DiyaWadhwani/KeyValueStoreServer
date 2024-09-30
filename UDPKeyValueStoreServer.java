import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UDPKeyValueStoreServer {
    private static final int BUFFER_SIZE = 1024;
    private static Map<String, String> keyValueStore = new HashMap<>();

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java UDPKeyValueStoreServer <port>");
            return;
        }

        int port = Integer.parseInt(args[0]);

        DatagramSocket socket = new DatagramSocket(port);
        byte[] buffer = new byte[BUFFER_SIZE];

        System.out.println("UDP Server listening on port " + port);

        while (true) {
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            socket.receive(request);

            String receivedMessage = new String(request.getData(), 0, request.getLength());
            String responseMessage;

            // If the request is an EXIT command, break the loop and close the server
            if (receivedMessage.equalsIgnoreCase("EXIT")) {
                System.out.println("Server is shutting down.");
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

            log("Received and processed: " + receivedMessage + " from " + request.getAddress() + ":" + request.getPort());
        }

        socket.close();
    }

    private static String processRequest(String message) {
        String[] parts = message.split(" ");
        
        // Check for malformed requests
        if (parts.length < 2) {
            log("Received malformed request of length " + message.length() + 
                " from " + "unknown address");
            return "Invalid request format";
        }

        String command = parts[0];
        String key = parts[1];
        String value = parts.length > 2 ? parts[2] : null;

        switch (command.toUpperCase()) {
            case "PUT":
                if (value != null) {
                    keyValueStore.put(key, value);
                    return "PUT successful: " + key + " = " + value;
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

    private static void log(String message) {
        String timestamp = new SimpleDateFormat("HH:mm:ss.SSS").format(new Date());
        System.out.println("[" + timestamp + "] " + message);
    }
}
