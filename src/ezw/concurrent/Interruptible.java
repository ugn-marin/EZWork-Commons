package ezw.concurrent;

import ezw.function.UnsafeRunnable;
import ezw.function.UnsafeSupplier;

import java.util.concurrent.ExecutorService;

/**
 * Utility methods and interfaces for handling interruptible flows.
 */
public abstract class Interruptible {

    private Interruptible() {}

    /**
     * An unsafe supplier throwing <code>InterruptedException</code>.
     */
    public interface InterruptibleSupplier<V> extends UnsafeSupplier<V> {

        @Override
        V get() throws InterruptedException;
    }

    /**
     * A functional runnable throwing <code>InterruptedException</code>.
     */
    @FunctionalInterface
    public interface InterruptibleRunnable extends UnsafeRunnable {

        @Override
        void run() throws InterruptedException;
    }

    /**
     * Runs the interruptible supplier, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>.
     * @param interruptible The interruptible callable.
     * @param <V> The callable result type.
     * @return The callable result.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static <V> V get(InterruptibleSupplier<V> interruptible) throws InterruptedRuntimeException {
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
     * Interruptible.run(object::wait)
     * </pre>
     * @param object The object.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void wait(Object object) throws InterruptedRuntimeException {
        run(object::wait);
    }

    /**
     * Runs the <code>sleep</code> method, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.run(() -> Thread.sleep(millis))
     * </pre>
     * @param millis The length of time to sleep in milliseconds.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void sleep(long millis) throws InterruptedRuntimeException {
        run(() -> Thread.sleep(millis));
    }

    /**
     * Runs the <code>join</code> method with the executor service, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.run(() -> Concurrent.join(executorService))
     * </pre>
     * @param executorService The executor service.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void join(ExecutorService executorService) throws InterruptedRuntimeException {
        run(() -> Concurrent.join(executorService));
    }

    /**
     * Runs the limiter's <code>begin</code> method, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.run(limiter::begin)
     * </pre>
     * @param limiter The limiter.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void begin(Limiter limiter) throws InterruptedRuntimeException {
        run(limiter::begin);
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
     * Interruptible.run(Interruptible::validateInterrupted)
     * </pre>
     * @throws InterruptedRuntimeException If this thread is marked as interrupted.
     */
    public static void validateInterruptedRuntime() throws InterruptedRuntimeException {
        run(Interruptible::validateInterrupted);
    }
}
