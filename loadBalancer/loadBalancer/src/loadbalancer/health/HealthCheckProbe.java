package loadbalancer.health;

import loadbalancer.core.Backend;

import java.time.Duration;

/** Abstraction over "is this backend alive?" so the check mechanism can be swapped out. */
public interface HealthCheckProbe {

    boolean check(Backend backend, String path, Duration timeout);
}
