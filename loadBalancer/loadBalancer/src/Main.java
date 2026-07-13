/** Composition root: wires up the load balancer's collaborators and starts it. */
public final class Main {

    public static void main(String[] args) {
        if (args.length == 1 && (args[0].equals("--help") || args[0].equals("-h"))) {
            System.out.println(LoadBalancerConfig.USAGE);
            return;
        }

        LoadBalancerConfig config;
        try {
            config = LoadBalancerConfig.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid arguments: " + e.getMessage());
            System.err.println(LoadBalancerConfig.USAGE);
            System.exit(1);
            return;
        }

        ServerPool serverPool = new ServerPool(config.getBackends());
        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ProxyForwarder forwarder = new ProxyForwarder();
        HealthCheckProbe probe = new HttpHealthCheckProbe();
        HealthChecker healthChecker = new HealthChecker(
                serverPool, probe, config.getHealthCheckPath(),
                config.getHealthCheckInterval(), config.getHealthCheckTimeout());
        LoadBalancerServer server = new LoadBalancerServer(config.getPort(), serverPool, strategy, forwarder);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down load balancer...");
            healthChecker.stop();
            server.stop();
        }));

        healthChecker.start();
        server.start();
    }
}
