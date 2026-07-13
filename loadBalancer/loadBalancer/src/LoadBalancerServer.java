import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** Accepts client connections and hands each one to a {@link ConnectionHandler}. */
public final class LoadBalancerServer {

    private final int port;
    private final ServerPool serverPool;
    private final LoadBalancingStrategy strategy;
    private final ProxyForwarder forwarder;
    private final ExecutorService connectionPool = Executors.newCachedThreadPool();

    private volatile ServerSocket serverSocket;
    private volatile boolean running;

    public LoadBalancerServer(int port, ServerPool serverPool, LoadBalancingStrategy strategy,
                               ProxyForwarder forwarder) {
        this.port = port;
        this.serverPool = serverPool;
        this.strategy = strategy;
        this.forwarder = forwarder;
    }

    public void start() {
        running = true;
        try (ServerSocket socket = new ServerSocket(port)) {
            serverSocket = socket;
            System.out.println("Load balancer listening on port " + port);
            while (running) {
                try {
                    Socket clientSocket = socket.accept();
                    connectionPool.submit(new ConnectionHandler(clientSocket, serverPool, strategy, forwarder));
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error accepting connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to start load balancer on port " + port, e);
        } finally {
            connectionPool.shutdown();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {
            // best-effort: we're shutting down anyway
        }
    }
}
