import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Owns the set of backends the load balancer knows about and exposes
 * read-safe views of them. Health state lives on {@link Backend} itself,
 * so this class only needs to worry about membership.
 */
public final class ServerPool {

    private final List<Backend> backends;

    public ServerPool(List<Backend> backends) {
        this.backends = new CopyOnWriteArrayList<>(backends);
    }

    public List<Backend> allBackends() {
        return List.copyOf(backends);
    }

    public List<Backend> healthyBackends() {
        List<Backend> healthy = new ArrayList<>();
        for (Backend backend : backends) {
            if (backend.isHealthy()) {
                healthy.add(backend);
            }
        }
        return healthy;
    }
}
