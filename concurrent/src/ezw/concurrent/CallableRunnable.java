package ezw.concurrent;

import java.util.concurrent.Callable;

/**
 * A functional runnable allowing exceptions, and is convertible to callable.
 */
@FunctionalInterface
public interface CallableRunnable {

    void run() throws Exception;

    default Callable<Void> toCallable() {
        return () -> {
            run();
            return null;
        };
    }
}
