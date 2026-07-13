package loadbalancer.config;

import loadbalancer.core.Backend;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/** Parses and holds command-line configuration for the load balancer. */
public final class LoadBalancerConfig {

    public static final String USAGE = "Usage: lb [--port <port>] [--backend host:port]... "
            + "[--health-check-path <path>] [--health-check-interval <seconds>] "
            + "[--health-check-timeout <seconds>]";

    private final int port;
    private final List<Backend> backends;
    private final String healthCheckPath;
    private final Duration healthCheckInterval;
    private final Duration healthCheckTimeout;

    private LoadBalancerConfig(int port, List<Backend> backends, String healthCheckPath,
                                Duration healthCheckInterval, Duration healthCheckTimeout) {
        this.port = port;
        this.backends = backends;
        this.healthCheckPath = healthCheckPath;
        this.healthCheckInterval = healthCheckInterval;
        this.healthCheckTimeout = healthCheckTimeout;
    }

    public static LoadBalancerConfig parse(String[] args) {
        int port = 8000;
        List<Backend> backends = new ArrayList<>();
        String healthCheckPath = "/";
        long intervalSeconds = 10;
        long timeoutSeconds = 2;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            switch (arg) {
                case "--port" -> port = Integer.parseInt(requireValue(args, ++i, arg));
                case "--backend" -> backends.add(parseBackend(requireValue(args, ++i, arg)));
                case "--health-check-path" -> healthCheckPath = requireValue(args, ++i, arg);
                case "--health-check-interval" -> intervalSeconds = Long.parseLong(requireValue(args, ++i, arg));
                case "--health-check-timeout" -> timeoutSeconds = Long.parseLong(requireValue(args, ++i, arg));
                default -> throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        if (backends.isEmpty()) {
            backends.add(new Backend("localhost", 8080));
            backends.add(new Backend("localhost", 8081));
        }

        return new LoadBalancerConfig(port, backends, healthCheckPath,
                Duration.ofSeconds(intervalSeconds), Duration.ofSeconds(timeoutSeconds));
    }

    private static String requireValue(String[] args, int index, String flag) {
        if (index >= args.length) {
            throw new IllegalArgumentException("Missing value for argument: " + flag);
        }
        return args[index];
    }

    private static Backend parseBackend(String hostPort) {
        String[] parts = hostPort.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Backend must be in host:port format, got: " + hostPort);
        }
        return new Backend(parts[0], Integer.parseInt(parts[1]));
    }

    public int getPort() {
        return port;
    }

    public List<Backend> getBackends() {
        return backends;
    }

    public String getHealthCheckPath() {
        return healthCheckPath;
    }

    public Duration getHealthCheckInterval() {
        return healthCheckInterval;
    }

    public Duration getHealthCheckTimeout() {
        return healthCheckTimeout;
    }
}
