package wish.list.server;

import wish.list.command.CommandCreator;
import wish.list.command.CommandExecutor;
import wish.list.storage.InMemoryStorage;
import wish.list.storage.Storage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Server class handles the lifecycle of a TCP server that listens for client connections,
 * processes commands from clients, and interacts with a {@link Storage} to manage items.
 * It supports handling multiple client connections and automatic server shutdown after a period of inactivity.
 */
public class Server {
    // The default port number the server listens on.
    private static final int PORT = 6666;

    // The size of the buffer used for reading and writing data.
    private static final int BUFFER_SIZE = 1024;

    // The host address the server binds to.
    private static final String HOST = "localhost";

    // The executor responsible for executing commands from clients.
    private final CommandExecutor commandExecutor;

    // The port number on which the server listens.
    private final int port;

    // Flag indicating whether the server is running.
    private boolean isServerWorking;

    // Buffer used for reading and writing data to/from clients.
    private ByteBuffer buffer;

    // The selector used to multiplex I/O operations.
    private Selector selector;

    // Thread-safe counter for the number of connected clients.
    private AtomicInteger connectedClients = new AtomicInteger(0);

    // Daemon thread timer for automatically shutting down the server after a period of inactivity.
    private Timer shutdownTimer = new Timer(true);

    // The maximum idle time (in milliseconds) before the server shuts down.
    private static final int SHUTDOWN_DELAY_MS = 10000;

    /**
     * Constructs a new {@link Server} instance.
     *
     * @param port            The port number to bind the server to.
     * @param commandExecutor The command executor used to process client commands.
     */
    public Server(int port, CommandExecutor commandExecutor) {
        this.port = port;
        this.commandExecutor = commandExecutor;
    }

    /**
     * Checks if the server is currently working (i.e., reachable or responsive).
     * This method returns a boolean indicating the status of the server, typically
     * representing whether the server is operational or not.
     *
     * @return {@code true} if the server is working (responsive or reachable),
     * {@code false} otherwise.
     */
    public boolean isServerWorking() {
        return isServerWorking;
    }

    /**
     * Starts the server, initializing resources and entering the main loop to handle client requests.
     * It accepts client connections and processes incoming data, executing commands and sending responses.
     */
    public void start() {
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            selector = Selector.open();
            configureServerSocketChannel(serverSocketChannel, selector);
            this.buffer = ByteBuffer.allocate(BUFFER_SIZE);

            isServerWorking = true;
            startShutdownTimer();

            while (isServerWorking) {
                processReadyChannels();
            }
        } catch (IOException e) {
            throw new UncheckedIOException("failed to start server", e);
        }
    }

    /**
     * Processes channels that are ready for I/O operations (e.g., reading or accepting connections).
     */
    private void processReadyChannels() {
        try {
            int readyChannels = selector.select();
            if (readyChannels == 0) {
                return;
            }

            Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    handleReadableKey(key);
                } else if (key.isAcceptable()) {
                    accept(selector, key);
                }

                keyIterator.remove();
            }
        } catch (IOException e) {
            System.out.println("Error occurred while processing client request: " + e.getMessage());
        }
    }

    /**
     * Handles a readable channel, reading input from the client and executing commands.
     *
     * @param key The selection key associated with the readable channel.
     * @throws IOException If an I/O error occurs while reading from the client channel.
     */
    private void handleReadableKey(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        String clientInput = getClientInput(clientChannel);
        System.out.println(clientInput);

        if (clientInput == null) {
            connectedClients.decrementAndGet();
            checkForShutdown();
            return;
        }

        String output = commandExecutor.execute(CommandCreator.newCommand(clientInput));
        writeClientOutput(clientChannel, output);
    }

    /**
     * Checks if the server should shut down due to inactivity.
     * The server shuts down when there are no connected clients.
     */
    private void checkForShutdown() {
        if (connectedClients.get() == 0) {
            startShutdownTimer();
        } else {
            shutdownTimer.cancel();
        }
    }

    /**
     * Starts the shutdown timer, which will shut down the server after the specified idle time.
     */
    private void startShutdownTimer() {
        shutdownTimer.cancel();
        shutdownTimer = new Timer(true);
        shutdownTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("Shutting down server due to inactivity...");
                stop();
            }
        }, SHUTDOWN_DELAY_MS);
    }

    /**
     * Stops the server and closes the selector.
     */
    public void stop() {
        this.isServerWorking = false;
        if (selector.isOpen()) {
            selector.wakeup();
        }
    }

    /**
     * Configures the server socket channel to bind to the specified host and port,
     * and register it with the selector to listen for incoming connections.
     *
     * @param channel  The server socket channel to configure.
     * @param selector The selector to register the channel with.
     * @throws IOException If an error occurs while configuring the server socket.
     */
    private void configureServerSocketChannel(ServerSocketChannel channel, Selector selector) throws IOException {
        channel.bind(new InetSocketAddress(HOST, this.port));
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * Reads input from a client channel.
     *
     * @param clientChannel The channel from which to read the input.
     * @return The client input as a string, or {@code null} if the client has disconnected.
     * @throws IOException If an error occurs while reading from the channel.
     */
    private String getClientInput(SocketChannel clientChannel) throws IOException {
        buffer.clear();

        int readBytes = clientChannel.read(buffer);
        if (readBytes < 0) {
            clientChannel.close();
            return null;
        }

        buffer.flip();

        byte[] clientInputBytes = new byte[buffer.remaining()];
        buffer.get(clientInputBytes);

        return new String(clientInputBytes, StandardCharsets.UTF_8);
    }

    /**
     * Writes output to a client channel.
     *
     * @param clientChannel The channel to write the output to.
     * @param output        The output string to send to the client.
     * @throws IOException If an error occurs while writing to the channel.
     */
    private void writeClientOutput(SocketChannel clientChannel, String output) throws IOException {
        buffer.clear();
        buffer.put(output.getBytes());
        buffer.flip();

        clientChannel.write(buffer);
    }

    /**
     * Accepts an incoming client connection and registers the client channel with the selector.
     * The method ensures that the channel is properly configured and client is counted for potential shutdown.
     *
     * @param selector The selector to register the client channel with.
     * @param key      The selection key associated with the server socket channel.
     * @throws IOException If an error occurs while accepting the client connection or configuring the channel.
     */
    private void accept(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel sockChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = null;

        try {
            clientChannel = sockChannel.accept();

            if (clientChannel != null) {
                clientChannel.configureBlocking(false);
                clientChannel.register(selector, SelectionKey.OP_READ);

                // Client connected
                connectedClients.incrementAndGet();
                shutdownTimer.cancel();
            }
        } catch (IOException e) {
            System.err.println("Error while accepting client connection: " + e.getMessage());

            if (clientChannel != null && clientChannel.isOpen()) {
                try {
                    clientChannel.close();
                } catch (IOException closeException) {
                    System.err.println("Error closing client channel: " + closeException.getMessage());
                }
            }
        }
    }

    /**
     * Main method that initializes the server and starts it.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Storage storage = new InMemoryStorage();
        CommandExecutor executor = new CommandExecutor(storage);
        Server server = new Server(PORT, executor);
        server.start();
    }
