package ezw.concurrent;

import java.util.concurrent.Callable;

/**
 * A functional runnable allowing exceptions, and is convertible to Void Callable.
 */
@FunctionalInterface
public interface CallableRunnable {

    void run() throws Exception;

    /**
     * Wraps this runnable execution in a Callable.
     */
    default Callable<Void> toVoidCallable() {
        return () -> {
            run();
            return null;
        };
    }
}
