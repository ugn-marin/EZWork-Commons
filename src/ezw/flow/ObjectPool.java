package ezw.flow;

import ezw.Sugar;
import ezw.function.Reducer;
import ezw.function.UnsafeConsumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * A simple object pool.
 * @param <T> The object type.
 */
public class ObjectPool<T> implements Supplier<T>, Consumer<T> {
    private final Supplier<T> supplier;
    private final Queue<T> objectsQueue = new ConcurrentLinkedQueue<>();

    /**
     * Constructs an empty object pool.
     * @param supplier The objects supplier.
     */
    public ObjectPool(Supplier<T> supplier) {
        this(supplier, 0);
    }

    /**
     * Constructs an object pool with an initial set of objects.
     * @param supplier The objects supplier.
     * @param initialSize The initial count of objects in the pool.
     */
    public ObjectPool(Supplier<T> supplier, int initialSize) {
        this.supplier = Objects.requireNonNull(supplier, "Supplier is null.");
        Sugar.fill(initialSize, supplier).forEach(this);
    }

    /**
     * Gets a used object from the pool, or supplies a new one if the pool is empty.
     * @return The object.
     */
    @Override
    public T get() {
        return Objects.requireNonNullElseGet(objectsQueue.poll(), supplier);
    }

    /**
     * Accepts an object back into the pool.
     * @param object The object.
     */
    @Override
    public void accept(T object) {
        objectsQueue.add(object);
    }

    /**
     * Returns the number of objects in the pool.
     */
    public int size() {
        return objectsQueue.size();
    }

    /**
     * Drains the pool while performing an action on each remaining object.
     * @param action The action to perform on each remaining object.
     * @throws Exception A reduced exception of the action calls.
     */
    public void drain(UnsafeConsumer<T> action) throws Exception {
        drain(action, Reducer.suppressor());
    }

    /**
     * Drains the pool while performing an action on each remaining object.
     * @param action The action to perform on each remaining object.
     * @param exceptionReducer A reducer for the action calls' exceptions.
     * @throws Exception A reduced exception of the action calls.
     */
    public void drain(UnsafeConsumer<T> action, Reducer<Exception> exceptionReducer) throws Exception {
        List<Exception> exceptions = new ArrayList<>(size());
        T object = objectsQueue.poll();
        while (object != null) {
            try {
                action.accept(object);
            } catch (Exception e) {
                exceptions.add(e);
            }
            object = objectsQueue.poll();
        }
        Sugar.throwIfNonNull(exceptionReducer.apply(exceptions));
    }

    /**
     * Clears the pool.
     */
    public void clear() {
        objectsQueue.clear();
    }
}
