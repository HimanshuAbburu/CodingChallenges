package loadbalancer.health;

import loadbalancer.core.Backend;
import loadbalancer.core.ServerPool;
import loadbalancer.logging.Logger;

import java.time.Duration;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Periodically probes every known backend (healthy or not) so that failures
 * are detected and recoveries are picked back up automatically.
 */
public final class HealthChecker {

    private final ServerPool serverPool;
    private final HealthCheckProbe probe;
    private final String healthCheckPath;
    private final Duration interval;
    private final Duration timeout;
    private final ScheduledExecutorService scheduler;
    private final Logger logger;

    public HealthChecker(ServerPool serverPool, HealthCheckProbe probe, String healthCheckPath,
                          Duration interval, Duration timeout, ScheduledExecutorService scheduler, Logger logger) {
        this.serverPool = serverPool;
        this.probe = probe;
        this.healthCheckPath = healthCheckPath;
        this.interval = interval;
        this.timeout = timeout;
        this.scheduler = scheduler;
        this.logger = logger;
    }

    public void start() {
        scheduler.scheduleAtFixedRate(this::checkAll, 0, interval.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }

    private void checkAll() {
        for (Backend backend : serverPool.allBackends()) {
            boolean isHealthy = probe.check(backend, healthCheckPath, timeout);
            boolean wasHealthy = backend.isHealthy();

            if (isHealthy && !wasHealthy) {
                backend.markHealthy();
                logger.info("Backend " + backend + " passed health check, back in rotation");
            } else if (!isHealthy && wasHealthy) {
                backend.markUnhealthy();
                logger.info("Backend " + backend + " failed health check, taking out of rotation");
            }
        }
    }
}
