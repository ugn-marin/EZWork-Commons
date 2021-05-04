package ezw.concurrent;

import java.util.concurrent.*;

public abstract class Concurrent {
    private static final ExecutorService generalPool = Executors.newWorkStealingPool(
            Runtime.getRuntime().availableProcessors());

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(generalPool::shutdown));
    }

    private Concurrent() {}

    public static Future<?> submit(Runnable task) {
        return generalPool.submit(task);
    }

    public static <T> Future<T> submit(Callable<T> task) {
        return generalPool.submit(task);
    }

    public static <T> Future<T> submit(Runnable task, T result) {
        return generalPool.submit(task, result);
    }

    public static void getAll(Future<?>... futures) throws ExecutionException, InterruptedException {
        for (var future : futures) {
            future.get();
        }
    }
}
