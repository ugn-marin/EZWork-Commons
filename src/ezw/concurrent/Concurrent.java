package ezw.concurrent;

import ezw.util.Lazy;

import java.util.concurrent.*;

/**
 * This class provides a general purpose thread pool for short computational tasks, and some concurrency utilities.
 */
public abstract class Concurrent {
    private static final Lazy<ExecutorService> generalPool = new Lazy<>(() -> {
        ExecutorService generalPool = Executors.newWorkStealingPool();
        Runtime.getRuntime().addShutdownHook(new Thread(generalPool::shutdown));
        return generalPool;
    });

    private Concurrent() {}

    /**
     * Returns the general purpose thread pool.
     */
    public static ExecutorService generalPool() {
        return generalPool.get();
    }

    /**
     * Submits a runnable into the general purpose thread pool.
     * @param task The task.
     * @return The task's future.
     */
    public static Future<?> submit(Runnable task) {
        return generalPool().submit(task);
    }

    /**
     * Submits a callable into the general purpose thread pool.
     * @param task The task.
     * @param <T> The task's result type.
     * @return The task's future.
     */
    public static <T> Future<T> submit(Callable<T> task) {
        return generalPool().submit(task);
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
