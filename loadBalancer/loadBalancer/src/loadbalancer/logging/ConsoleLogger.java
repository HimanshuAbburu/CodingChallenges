package loadbalancer.logging;

/** Writes log messages to standard out / standard error. */
public final class ConsoleLogger implements Logger {

    @Override
    public void info(String message) {
        System.out.println(message);
    }

    @Override
    public void error(String message) {
        System.err.println(message);
    }
}
