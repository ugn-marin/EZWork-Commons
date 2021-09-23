package ezw.concurrent;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * Utility methods and interfaces for handling interruptible flows.
 */
public abstract class Interruptible {

    private Interruptible() {}

    /**
     * A callable throwing <code>InterruptedException</code>.
     */
    public interface InterruptibleCallable<V> extends Callable<V> {

        @Override
        V call() throws InterruptedException;

        /**
         * Constructs a supplier wrapper, wrapping the <code>InterruptedException</code> in an
         * <code>InterruptedRuntimeException</code>.
         */
        default Supplier<V> toSupplier() {
            return () -> {
                try {
                    return call();
                } catch (InterruptedException e) {
                    throw new InterruptedRuntimeException(e);
                }
            };
        }
    }

    /**
     * A functional runnable throwing <code>InterruptedException</code>.
     */
    @FunctionalInterface
    public interface InterruptibleRunnable {

        void run() throws InterruptedException;

        /**
         * Constructs a runnable wrapper, wrapping the <code>InterruptedException</code> in an
         * <code>InterruptedRuntimeException</code>.
         */
        default Runnable toRunnable() {
            return () -> {
                try {
                    run();
                } catch (InterruptedException e) {
                    throw new InterruptedRuntimeException(e);
                }
            };
        }
    }

    /**
     * Calls the interruptible callable, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>.
     * @param interruptible The interruptible callable.
     * @param <V> The callable result type.
     * @return The callable result.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static <V> V call(InterruptibleCallable<V> interruptible) throws InterruptedRuntimeException {
        return interruptible.toSupplier().get();
    }

    /**
     * Runs the interruptible runnable, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>.
     * @param interruptible The interruptible runnable.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void run(InterruptibleRunnable interruptible) throws InterruptedRuntimeException {
        interruptible.toRunnable().run();
    }

    /**
     * Runs the object's <code>wait</code> method, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.run(object::wait);
     * </pre>
     * @param object The object.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void wait(Object object) throws InterruptedRuntimeException {
        run(object::wait);
    }

    /**
     * Runs the limiter's <code>begin</code> method, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.run(limiter::begin);
     * </pre>
     * @param limiter The limiter.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void begin(Limiter limiter) throws InterruptedRuntimeException {
        run(limiter::begin);
    }

    /**
     * Runs the <code>sleep</code> method, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.run(() -> Thread.sleep(millis));
     * </pre>
     * @param millis The length of time to sleep in milliseconds.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void sleep(long millis) throws InterruptedRuntimeException {
        run(() -> Thread.sleep(millis));
    }

    /**
     * Validates the interrupted status of the thread, and throws InterruptedException if set. The interrupted status of
     * the thread is unaffected by this method.
     * @throws InterruptedException If this thread is marked as interrupted.
     */
    public static void validateInterrupted() throws InterruptedException {
        if (Thread.currentThread().isInterrupted())
            throw new InterruptedException("Interruption detected.");
    }

    /**
     * Validates the interrupted status of the thread, and throws InterruptedRuntimeException if set. The interrupted
     * status of the thread is unaffected by this method. Equivalent to:
     * <pre>
     * Interruptible.run(Interruptible::validateInterrupted);
     * </pre>
     * @throws InterruptedRuntimeException If this thread is marked as interrupted.
     */
    public static void validateInterruptedRuntime() throws InterruptedRuntimeException {
        run(Interruptible::validateInterrupted);
    }
}
