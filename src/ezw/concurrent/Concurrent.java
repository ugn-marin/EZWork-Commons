package ezw.concurrent;

import ezw.Sugar;
import ezw.function.Reducer;
import ezw.function.UnsafeRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Various concurrency utilities.
 */
public abstract class Concurrent {
    private static final Lazy<ExecutorService> cachedPool = new Lazy<>(() -> Executors.newCachedThreadPool(
            namedThreadFactory(Concurrent.class.getSimpleName())));

    private Concurrent() {}

    /**
     * Submits an unsafe runnable into the cached pool.
     * @param task A task.
     * @return The task's future.
     */
    public static Future<Void> run(UnsafeRunnable task) {
        return run(task.toVoidCallable());
    }

    /**
     * Submits a callable into the cached pool.
     * @param task A task.
     * @param <T> The task's result type.
     * @return The task's future.
     */
    public static <T> Future<T> run(Callable<T> task) {
        return cachedPool.get().submit(task);
    }

    /**
     * Submits several unsafe runnable tasks into the cached pool, waits for all tasks completion.
     * @param exceptionsReducer A reducer of the tasks exceptions list, returning the exception to throw.
     * @param tasks The tasks.
     */
    public static void run(Reducer<Exception> exceptionsReducer, Runnable... tasks) throws Exception {
        getAll(exceptionsReducer, Arrays.stream(Sugar.requireFull(tasks)).map(task -> run(task::run))
                .toArray(Future[]::new));
    }

    /**
     * Calls <code>get</code> for each future. In other words, waits if necessary for all futures' tasks completion.
     * @param exceptionsReducer A reducer of the futures exceptions list, returning the exception to throw.
     * @param futures The futures.
     */
    public static void getAll(Reducer<Exception> exceptionsReducer, Future<?>... futures) throws Exception {
        Lazy<List<Exception>> exceptions = new Lazy<>(ArrayList::new);
        for (var future : Sugar.requireFull(futures)) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                exceptions.get().add(e);
            }
        }
        if (exceptions.isCalculated())
            Sugar.throwIfNonNull(exceptionsReducer.apply(exceptions.get()));
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
     * Constructs a thread factory naming the threads as name and thread number: <code>"name #"</code>.<br>
     * The numbers start appearing from 2, so single threaded pools have the thread name as just <code>"name"</code>.
     * @param name The name.
     * @return The thread factory.
     */
    public static ThreadFactory namedThreadFactory(String name) {
        return new NamedThreadFactory(name);
    }

    private static class NamedThreadFactory implements ThreadFactory {
        private final String name;
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger();

        NamedThreadFactory(String name) {
            this.name = Objects.requireNonNull(name, "Name is null.");
            var sm = System.getSecurityManager();
            group = sm != null ? sm.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        @Override
        public Thread newThread(Runnable task) {
            int number = threadNumber.incrementAndGet();
            String threadName = number > 1 ? name + " " + number : name;
            var thread = new Thread(group, task, threadName, 0);
            if (thread.isDaemon())
                thread.setDaemon(false);
            if (thread.getPriority() != Thread.NORM_PRIORITY)
                thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        }
    }
}
