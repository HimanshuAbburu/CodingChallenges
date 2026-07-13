package loadbalancer;

import loadbalancer.config.LoadBalancerConfig;
import loadbalancer.core.ServerPool;
import loadbalancer.health.HealthCheckProbe;
import loadbalancer.health.HealthChecker;
import loadbalancer.health.HttpHealthCheckProbe;
import loadbalancer.logging.ConsoleLogger;
import loadbalancer.logging.Logger;
import loadbalancer.proxy.ProxyForwarder;
import loadbalancer.server.LoadBalancerServer;
import loadbalancer.strategy.LoadBalancingStrategy;
import loadbalancer.strategy.RoundRobinStrategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

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

        Logger logger = new ConsoleLogger();
        ServerPool serverPool = new ServerPool(config.getBackends());
        LoadBalancingStrategy strategy = new RoundRobinStrategy();
        ProxyForwarder forwarder = new ProxyForwarder(logger);

        ExecutorService connectionPool = Executors.newCachedThreadPool();
        LoadBalancerServer server = new LoadBalancerServer(
                config.getPort(), serverPool, strategy, forwarder, connectionPool, logger);

        HealthCheckProbe probe = new HttpHealthCheckProbe();
        ScheduledExecutorService healthCheckScheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "health-checker");
            thread.setDaemon(true);
            return thread;
        });
        HealthChecker healthChecker = new HealthChecker(
                serverPool, probe, config.getHealthCheckPath(),
                config.getHealthCheckInterval(), config.getHealthCheckTimeout(),
                healthCheckScheduler, logger);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down load balancer...");
            healthChecker.stop();
            server.stop();
        }));

        healthChecker.start();
        server.start();
    }
}
