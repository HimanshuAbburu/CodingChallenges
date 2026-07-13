package loadbalancer.strategy;

import loadbalancer.core.Backend;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Cycles through the candidate list in order, wrapping back to the start. */
public final class RoundRobinStrategy implements LoadBalancingStrategy {

    private final AtomicInteger counter = new AtomicInteger(0);

    @Override
    public Backend select(List<Backend> candidates) {
        if (candidates.isEmpty()) {
            return null;
        }
        int index = Math.floorMod(counter.getAndIncrement(), candidates.size());
        return candidates.get(index);
    }
}
