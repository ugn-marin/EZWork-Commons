package ezw.util.function;

import ezw.util.Sugar;

import java.util.concurrent.Callable;

/**
 * A functional runnable allowing exceptions, and is convertible to Runnable and Void Callable.
 */
@FunctionalInterface
public interface UnsafeRunnable {

    void run() throws Exception;

    /**
     * Wraps this runnable implementation in a Runnable.
     */
    default Runnable toRunnable() {
        return () -> {
            try {
                run();
            } catch (Exception e) {
                throw Sugar.sneaky(e);
            }
        };
    }

    /**
     * Wraps this runnable implementation in a Void Callable.
     */
    default Callable<Void> toVoidCallable() {
        return () -> {
            run();
            return null;
        };
    }
}
