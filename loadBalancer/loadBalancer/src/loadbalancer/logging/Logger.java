package loadbalancer.logging;

/** Abstraction over "where do log messages go" so callers don't hard-code System.out/err. */
public interface Logger {

    void info(String message);

    void error(String message);
}
