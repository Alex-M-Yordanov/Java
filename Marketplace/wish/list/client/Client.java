package wish.list.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * The {@code Client} class represents a client application that connects to a server,
 * sends user input commands, and receives and displays the server's response.
 * <p>
 * The client connects to the server at the specified host and port, continuously
 * sending commands entered by the user. The user can exit the program by typing 'exit'.
 * </p>
 * <p>
 * This class uses non-blocking I/O with a {@link SocketChannel} to interact with the server.
 * </p>
 */
public class Client {

    // Server host address
    private static final String HOST = "localhost";

    // Server port
    private static final int PORT = 6666;

    // Size of the buffer for reading/writing data
    private static final int BUFFER_SIZE = 1024;

    /**
     * The entry point for the client application. This method connects to the server,
     * sends user input commands, and receives and prints the server's response.
     * <p>
     * The program repeatedly prompts the user for input until the user types 'exit'.
     * The input is sent to the server, and the server's response is displayed.
     * </p>
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try (SocketChannel clientChannel = SocketChannel.open(new InetSocketAddress(HOST, PORT));
             Scanner scanner = new Scanner(System.in)) {
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            System.out.println("Connected to the server. Type commands or 'exit' to quit.");
            while (true) {
                System.out.print("> ");
                String input = scanner.nextLine();
                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }

                /// Send command to server
                buffer.clear();  // Clear the buffer before writing new data
                buffer.put(input.getBytes(StandardCharsets.UTF_8));  // Put the user input in the buffer
                buffer.flip();  // Switch to read mode
                clientChannel.write(buffer);  // Send the data to the server

                /// Receive server response
                buffer.clear();  // Clear the buffer for reading the response
                clientChannel.read(buffer);  // Read the server response into the buffer
                buffer.flip();  // Switch to read mode
                byte[] responseBytes = new byte[buffer.remaining()];  // Create an array to hold the response
                buffer.get(responseBytes);  // Extract the bytes from the buffer
                System.out.println("Response: " + new String(responseBytes, StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            System.out.println("Connection to server failed: " + e.getMessage());
        }
    }
}
