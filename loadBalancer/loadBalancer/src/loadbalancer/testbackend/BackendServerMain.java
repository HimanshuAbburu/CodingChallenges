package loadbalancer.testbackend;

/** Standalone entry point for the "be" test backend, run independently of the load balancer. */
public final class BackendServerMain {

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        new BackendServer(port).start();
    }
}
