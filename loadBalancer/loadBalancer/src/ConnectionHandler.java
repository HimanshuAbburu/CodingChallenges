import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Handles one client connection end to end: reads the request, picks a
 * healthy backend, forwards it, and falls back to another healthy backend
 * (marking the failed one unhealthy immediately) if the exchange fails.
 */
public final class ConnectionHandler implements Runnable {

    private static final int CLIENT_READ_TIMEOUT_MS = 30_000;

    private final Socket clientSocket;
    private final ServerPool serverPool;
    private final LoadBalancingStrategy strategy;
    private final ProxyForwarder forwarder;

    public ConnectionHandler(Socket clientSocket, ServerPool serverPool,
                              LoadBalancingStrategy strategy, ProxyForwarder forwarder) {
        this.clientSocket = clientSocket;
        this.serverPool = serverPool;
        this.strategy = strategy;
        this.forwarder = forwarder;
    }

    @Override
    public void run() {
        try (clientSocket) {
            clientSocket.setSoTimeout(CLIENT_READ_TIMEOUT_MS);
            String clientIp = clientSocket.getInetAddress().getHostAddress();
            System.out.println("Received request from " + clientIp);

            Optional<RawHttpRequest> maybeRequest = RawHttpRequest.readFrom(clientSocket.getInputStream());
            if (maybeRequest.isEmpty()) {
                return;
            }
            RawHttpRequest request = maybeRequest.get();
            System.out.println(request.toLogString());

            OutputStream clientOut = clientSocket.getOutputStream();
            if (!forwardWithFailover(request, clientOut)) {
                writeServiceUnavailable(clientOut);
            }
        } catch (IOException e) {
            System.err.println("Error handling client connection: " + e.getMessage());
        }
    }

    private boolean forwardWithFailover(RawHttpRequest request, OutputStream clientOut) {
        List<Backend> attempted = new ArrayList<>();
        int maxAttempts = Math.max(serverPool.healthyBackends().size(), 1);

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            List<Backend> candidates = serverPool.healthyBackends();
            candidates.removeAll(attempted);
            if (candidates.isEmpty()) {
                return false;
            }

            Backend target = strategy.select(candidates);
            if (target == null) {
                return false;
            }
            attempted.add(target);

            System.out.println("Routing request to: " + target);
            try {
                forwarder.forward(request.getRawBytes(), target, clientOut);
                return true;
            } catch (IOException e) {
                System.err.println("Backend " + target + " unreachable, taking it out of rotation: " + e.getMessage());
                target.markUnhealthy();
            }
        }
        return false;
    }

    private void writeServiceUnavailable(OutputStream clientOut) throws IOException {
        String body = "No healthy backend servers available";
        String response = "HTTP/1.1 503 Service Unavailable\r\n"
                + "Content-Type: text/plain\r\n"
                + "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n"
                + "Connection: close\r\n\r\n"
                + body;
        clientOut.write(response.getBytes(StandardCharsets.UTF_8));
        clientOut.flush();
    }
}
