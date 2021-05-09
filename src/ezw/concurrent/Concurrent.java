package ezw.concurrent;

import java.util.concurrent.*;

/**
 * Various concurrency utilities.
 */
public abstract class Concurrent {

    private Concurrent() {}

    /**
     * Submits a callable runnable into the commonPool.
     * @param task A short calculation task.
     * @return The task's future.
     */
    public static Future<Void> calculate(CallableRunnable task) {
        return calculate(task.toCallable());
    }

    /**
     * Submits a callable into the commonPool.
     * @param task A short calculation task.
     * @param <T> The task's result type.
     * @return The task's future.
     */
    public static <T> Future<T> calculate(Callable<T> task) {
        return ForkJoinPool.commonPool().submit(task);
    }

    /**
     * Shuts the executor service down and awaits termination indefinitely.
     * @param executorService The executor service.
     * @throws InterruptedException If interrupted.
     */
    public static void join(ExecutorService executorService) throws InterruptedException {
        executorService.shutdown();
        if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS))
            throw new InterruptedException();
    }

    /**
     * Calls <code>get</code> for each future. In other words, waits if necessary for all futures' tasks completion.
     * @param futures The futures.
     * @throws ExecutionException If any of the futures' computation failed.
     * @throws InterruptedException If interrupted.
     */
    public static void getAll(Future<?>... futures) throws ExecutionException, InterruptedException {
        for (var future : futures) {
            future.get();
        }
    }
}
