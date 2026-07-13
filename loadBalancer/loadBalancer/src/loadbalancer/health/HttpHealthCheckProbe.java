package loadbalancer.health;

import loadbalancer.core.Backend;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/** Considers a backend healthy if a GET to its health check path returns HTTP 200. */
public final class HttpHealthCheckProbe implements HealthCheckProbe {

    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    @Override
    public boolean check(Backend backend, String path, Duration timeout) {
        try {
            URI uri = URI.create("http://" + backend.getHost() + ":" + backend.getPort() + path);
            HttpRequest request = HttpRequest.newBuilder(uri)
                    .timeout(timeout)
                    .GET()
                    .build();
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }
}
