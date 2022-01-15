package ezw.flow;

import java.util.function.Supplier;

/**
 * A simple object pool of auto-closeable objects.
 * @param <T> The auto-closable type.
 */
public class AutoCloseablePool<T extends AutoCloseable> extends ObjectPool<T> implements AutoCloseable {

    /**
     * Constructs an auto-closeable pool.
     * @param supplier The objects supplier.
     */
    public AutoCloseablePool(Supplier<T> supplier) {
        super(supplier);
    }

    /**
     * Constructs an auto-closeable pool with an initial set of objects.
     * @param supplier The objects supplier.
     * @param initialSize The initial count of objects in the pool.
     */
    public AutoCloseablePool(Supplier<T> supplier, int initialSize) {
        super(supplier, initialSize);
    }

    /**
     * Drains the pool while closing each remaining object.
     * @throws Exception A reduced exception of the <code>close</code> calls.
     */
    @Override
    public void close() throws Exception {
        drain(AutoCloseable::close);
    }
}
