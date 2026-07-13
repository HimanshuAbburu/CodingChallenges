import java.util.List;

/**
 * Strategy pattern: picks which backend should handle the next request.
 * Implementations receive only the currently healthy candidates, so they
 * don't need to know anything about health checking.
 */
public interface LoadBalancingStrategy {

    /** Returns the chosen backend, or {@code null} if candidates is empty. */
    Backend select(List<Backend> candidates);
}
