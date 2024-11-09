package wish.list;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import wish.list.command.CommandExecutor;
import wish.list.server.Server;

import java.io.IOException;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ServerTest {

    private static final int PORT = 6666;
    private static final int TIMEOUT = 1000;

    @Mock
    private CommandExecutor mockCommandExecutor;

    @Mock
    private ServerSocketChannel mockServerSocketChannel;

    @Mock
    private SocketChannel mockClientChannel;

    @Mock
    private Selector mockSelector;

    private Server server;
    private AutoCloseable mocks;

    @BeforeEach
    void setUp() throws IOException {
        mocks = MockitoAnnotations.openMocks(this);
        server = new Server(PORT, mockCommandExecutor);
    }

    // Closing mocks after each test preventing potential memory leaks
    @AfterEach
    void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    void testServerStartupAndShutdown() throws InterruptedException {
        try {
            Thread serverThread = new Thread(() -> server.start());
            serverThread.start();

            // Wait a moment for the server to start
            Thread.sleep(TIMEOUT);

            assertTrue(server.isServerWorking());
            server.stop();
            assertFalse(server.isServerWorking());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Test interrupted: " + e.getMessage());
        }
    }

}
