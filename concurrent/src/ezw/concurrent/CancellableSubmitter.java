package ezw.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A wrapper for an executor service, submitting tasks that can all be cancelled or interrupted at once.
 */
public class CancellableSubmitter {
    private final ExecutorService executorService;
    private final Map<Callable<?>, Future<?>> submittedFutures = new HashMap<>();

    /**
     * Creates a cancellable submitter.
     * @param executorService The executor service to submit the tasks to.
     */
    public CancellableSubmitter(ExecutorService executorService) {
        this.executorService = executorService;
    }

    /**
     * Submits a task into the executor service.
     * @param task The task to submit.
     * @param <V> The type of the task's result.
     * @return A Future representing pending completion of the task.
     */
    public <V> Future<V> submit(Callable<V> task) {
        AtomicBoolean registered = new AtomicBoolean();
        // Submit a wrapping task
        var future = executorService.submit(() -> {
            try {
                // First make sure registered
                synchronized (registered) {
                    while (!registered.get()) {
                        registered.wait();
                    }
                }
                // Execute
                return task.call();
            } finally {
                // Unregister the future
                synchronized (submittedFutures) {
                    submittedFutures.remove(task);
                }
            }
        });
        // Register the future for potential cancellation
        synchronized (submittedFutures) {
            submittedFutures.put(task, future);
        }
        // Mark registered
        synchronized (registered) {
            registered.set(true);
            registered.notifyAll();
        }
        return future;
    }

    /**
     * Attempts to cancel all futures of submitted or executing tasks. Attempts to interrupt executing tasks.
     */
    public void cancelSubmitted() {
        synchronized (submittedFutures) {
            submittedFutures.values().forEach(future -> future.cancel(true));
        }
    }
}
