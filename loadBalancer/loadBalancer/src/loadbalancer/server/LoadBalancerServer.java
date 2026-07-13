package loadbalancer.server;

import loadbalancer.core.ServerPool;
import loadbalancer.logging.Logger;
import loadbalancer.proxy.ConnectionHandler;
import loadbalancer.proxy.ProxyForwarder;
import loadbalancer.strategy.LoadBalancingStrategy;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

/** Accepts client connections and hands each one to a {@link ConnectionHandler}. */
public final class LoadBalancerServer {

    private final int port;
    private final ServerPool serverPool;
    private final LoadBalancingStrategy strategy;
    private final ProxyForwarder forwarder;
    private final ExecutorService connectionPool;
    private final Logger logger;

    private volatile ServerSocket serverSocket;
    private volatile boolean running;

    public LoadBalancerServer(int port, ServerPool serverPool, LoadBalancingStrategy strategy,
                               ProxyForwarder forwarder, ExecutorService connectionPool, Logger logger) {
        this.port = port;
        this.serverPool = serverPool;
        this.strategy = strategy;
        this.forwarder = forwarder;
        this.connectionPool = connectionPool;
        this.logger = logger;
    }

    public void start() {
        running = true;
        try (ServerSocket socket = new ServerSocket(port)) {
            serverSocket = socket;
            logger.info("Load balancer listening on port " + port);
            while (running) {
                try {
                    Socket clientSocket = socket.accept();
                    connectionPool.submit(new ConnectionHandler(clientSocket, serverPool, strategy, forwarder, logger));
                } catch (IOException e) {
                    if (running) {
                        logger.error("Error accepting connection: " + e.getMessage());
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
