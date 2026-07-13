package loadbalancer.core;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/** A single backend server the load balancer can route traffic to. */
public final class Backend {

    private final String host;
    private final int port;
    private final AtomicBoolean healthy;

    public Backend(String host, int port) {
        this(host, port, true);
    }

    public Backend(String host, int port, boolean initiallyHealthy) {
        this.host = host;
        this.port = port;
        this.healthy = new AtomicBoolean(initiallyHealthy);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public boolean isHealthy() {
        return healthy.get();
    }

    public void markHealthy() {
        healthy.set(true);
    }

    public void markUnhealthy() {
        healthy.set(false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Backend other)) return false;
        return port == other.port && host.equals(other.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(host, port);
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
