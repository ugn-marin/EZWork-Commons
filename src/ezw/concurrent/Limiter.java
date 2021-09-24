package ezw.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * A synchronization aid that allows one or more threads to wait until a set of operations being performed in other
 * threads completes, given that the number of registered operations has reached the defined limit. Operations are
 * registered using the {@link #begin} method, and unregistered using the {@link #end} method.
 */
public class Limiter {
    private final int limit;
    private final AtomicInteger executing = new AtomicInteger();

    /**
     * Constructs a limiter.
     * @param limit The limit.
     */
    public Limiter(int limit) {
        this.limit = limit;
    }

    /**
     * Returns the limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Returns the current registered operations count.
     */
    public int getExecuting() {
        return executing.get();
    }

    /**
     * Increases the count of registered operations if not reached the limit, else causes the current thread to wait for
     * the count to decrease.
     * @throws InterruptedException If interrupted.
     */
    public void begin() throws InterruptedException {
        Interruptible.validateInterrupted();
        synchronized (executing) {
            while (executing.get() == limit) {
                executing.wait();
            }
            executing.incrementAndGet();
        }
    }

    /**
     * Decreases the count of registered operations.
     */
    public void end() {
        synchronized (executing) {
            executing.decrementAndGet();
            executing.notifyAll();
        }
    }
}
