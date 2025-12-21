import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBalancer {
    private final int port;

    private static final int THREAD_POOL = 10;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(THREAD_POOL);

    public LoadBalancer(int port){
        this.port = port;
    }

    public void start(){
        try (ServerSocket serverSocket = new ServerSocket(port)){
            System.out.println("LB started on port: " + port);
            while (true){
                try(Socket clientSocket = serverSocket.accept()){
                    handleConnection(clientSocket);
                } catch (IOException e){
                    System.err.println("Failed to handle connection: " + e.getMessage());
                }
            }
        } catch (IOException e){
            throw new RuntimeException("Failed to start on port: " + port, e);
        }
    }

    private void handleConnection(Socket socket) throws IOException {
        System.out.println("Received request from " + socket.getInetAddress().getHostAddress());

        // Use the socket's streams
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8)
        );
        BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())
        );

        StringBuilder requestContents = new StringBuilder();
        String line;

        // Read the incoming request
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            requestContents.append(line).append("\r\n");
        }

        // Respond to curl so it doesn't hang or throw HTTP/0.9 errors
        writer.write("HTTP/1.1 200 OK\r\n");
        writer.write("Content-Type: text/plain\r\n");
        writer.write("Connection: close\r\n");
        writer.write("\r\n"); // End of headers

        // Echo the captured request back to the terminal
        writer.write(requestContents.toString());
//        System.out.println(requestContents.toString());
        writer.flush();
    }
}
