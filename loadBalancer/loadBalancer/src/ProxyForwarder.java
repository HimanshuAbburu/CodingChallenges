import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/** Sends a raw request to a single backend and streams its response back to the client. */
public final class ProxyForwarder {

    private static final int CONNECT_TIMEOUT_MS = 2000;
    private static final int READ_TIMEOUT_MS = 5000;
    private static final int BUFFER_SIZE = 8192;

    /** Throws IOException if the backend could not be reached or the exchange failed mid-flight. */
    public void forward(byte[] rawRequest, Backend backend, OutputStream clientOut) throws IOException {
        try (Socket backendSocket = new Socket()) {
            backendSocket.connect(new InetSocketAddress(backend.getHost(), backend.getPort()), CONNECT_TIMEOUT_MS);
            backendSocket.setSoTimeout(READ_TIMEOUT_MS);

            OutputStream backendOut = backendSocket.getOutputStream();
            InputStream backendIn = backendSocket.getInputStream();

            backendOut.write(rawRequest);
            backendOut.flush();

            byte[] buffer = new byte[BUFFER_SIZE];
            int bytesRead;
            boolean loggedHeader = false;
            while ((bytesRead = backendIn.read(buffer)) != -1) {
                if (!loggedHeader) {
                    System.out.print("Response from server: ");
                    loggedHeader = true;
                }
                System.out.print(new String(buffer, 0, bytesRead, StandardCharsets.ISO_8859_1));
                clientOut.write(buffer, 0, bytesRead);
            }
            System.out.println();
            clientOut.flush();
        }
    }
}
