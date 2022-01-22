package ezw.concurrent;

import ezw.function.UnsafeConsumer;
import ezw.function.UnsafeFunction;
import ezw.function.UnsafeRunnable;
import ezw.function.UnsafeSupplier;

import java.util.Objects;
import java.util.concurrent.ExecutorService;

/**
 * Utility methods and interfaces for handling interruptible flows, and wrapping interrupted exceptions if required.
 */
public abstract class Interruptible {

    private Interruptible() {}

    /**
     * An unsafe supplier throwing <code>InterruptedException</code>.
     */
    public interface InterruptibleSupplier<O> extends UnsafeSupplier<O> {

        @Override
        O get() throws InterruptedException;
    }

    /**
     * An unsafe consumer throwing <code>InterruptedException</code>.
     */
    public interface InterruptibleConsumer<I> extends UnsafeConsumer<I> {

        @Override
        void accept(I t) throws InterruptedException;
    }

    /**
     * An unsafe function throwing <code>InterruptedException</code>.
     */
    public interface InterruptibleFunction<I, O> extends UnsafeFunction<I, O> {

        @Override
        O apply(I t) throws InterruptedException;
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
     * @param interruptible The interruptible supplier.
     * @param <O> The supplier output type.
     * @return The supplier output.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static <O> O get(InterruptibleSupplier<O> interruptible) throws InterruptedRuntimeException {
        return Objects.requireNonNull(interruptible, "Interruptible is null.").toSupplier().get();
    }

    /**
     * Runs the interruptible consumer, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>.
     * @param interruptible The interruptible consumer.
     * @param t The consumer input.
     * @param <I> The consumer input type.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static <I> void accept(InterruptibleConsumer<I> interruptible, I t) throws InterruptedRuntimeException {
        Objects.requireNonNull(interruptible, "Interruptible is null.").toConsumer().accept(t);
    }

    /**
     * Runs the interruptible function, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>.
     * @param interruptible The interruptible function.
     * @param t The function input.
     * @param <I> The function input type.
     * @param <O> The function output type.
     * @return The function output.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static <I, O> O apply(InterruptibleFunction<I, O> interruptible, I t) throws InterruptedRuntimeException {
        return Objects.requireNonNull(interruptible, "Interruptible is null.").toFunction().apply(t);
    }

    /**
     * Runs the interruptible runnable, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>.
     * @param interruptible The interruptible runnable.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void run(InterruptibleRunnable interruptible) throws InterruptedRuntimeException {
        Objects.requireNonNull(interruptible, "Interruptible is null.").toRunnable().run();
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
     * Runs the object's <code>wait</code> method, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.accept(object::wait, millis)
     * </pre>
     * @param object The object.
     * @param millis The maximum time to wait in milliseconds.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void wait(Object object, long millis) throws InterruptedRuntimeException {
        accept(object::wait, millis);
    }

    /**
     * Runs the <code>sleep</code> method, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.accept(Thread::sleep, millis)
     * </pre>
     * @param millis The length of time to sleep in milliseconds.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void sleep(long millis) throws InterruptedRuntimeException {
        accept(Thread::sleep, millis);
    }

    /**
     * Runs the <code>join</code> method with the executor service, wrapping the <code>InterruptedException</code> in an
     * <code>InterruptedRuntimeException</code>. Equivalent to:
     * <pre>
     * Interruptible.accept(Concurrent::join, executorService)
     * </pre>
     * @param executorService The executor service.
     * @throws InterruptedRuntimeException If interrupted.
     */
    public static void join(ExecutorService executorService) throws InterruptedRuntimeException {
        accept(Concurrent::join, executorService);
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
