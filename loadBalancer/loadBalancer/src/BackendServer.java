import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal standalone HTTP server used to exercise the load balancer against.
 * Responds 200 OK with a fixed body to any request, matching the "be" test
 * server described in the challenge.
 */
public final class BackendServer {

    private final int port;
    private final String responseBody;
    private final ExecutorService connectionPool = Executors.newCachedThreadPool();

    public BackendServer(int port) {
        this(port, "Hello From Backend Server");
    }

    public BackendServer(int port, String responseBody) {
        this.port = port;
        this.responseBody = responseBody;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Backend server listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                connectionPool.submit(() -> handleConnection(socket));
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not start backend server on port " + port, e);
        }
    }

    private void handleConnection(Socket socket) {
        try (socket) {
            System.out.println("Received request from " + socket.getInetAddress().getHostAddress());

            Optional<RawHttpRequest> maybeRequest = RawHttpRequest.readFrom(socket.getInputStream());
            if (maybeRequest.isEmpty()) {
                return;
            }
            System.out.println(maybeRequest.get().toLogString());

            byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);
            String headers = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/plain\r\n"
                    + "Content-Length: " + body.length + "\r\n"
                    + "Connection: close\r\n\r\n";

            OutputStream out = socket.getOutputStream();
            out.write(headers.getBytes(StandardCharsets.UTF_8));
            out.write(body);
            out.flush();

            System.out.println("Replied with a hello message");
        } catch (IOException e) {
            System.err.println("Error handling backend request: " + e.getMessage());
        }
    }
}
