package ezw.concurrent;

import ezw.util.Sugar;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A lazy supplier decorator.
 * @param <T> The value type.
 */
public class Lazy<T> implements Supplier<T> {
    private final Supplier<T> valueSupplier;
    private boolean isCalculated;
    private T value;

    /**
     * Constructs a lazy supplier.
     * @param valueSupplier The value supplier. Will be calculated on the first attempt to get the value. If the
     *                      calculation fails, the value will remain null.
     */
    public Lazy(Supplier<T> valueSupplier) {
        this.valueSupplier = Objects.requireNonNull(valueSupplier, "Value supplier cannot be null.");
    }

    /**
     * Constructs a lazy supplier.
     * @param callable A callable supplying the value. Will be calculated on the first attempt to get the value.
     * @param onException A function returning a value if the callable throws an exception.
     */
    public Lazy(Callable<T> callable, Function<Exception, T> onException) {
        this(Sugar.orElse(callable, onException));
    }

    @Override
    public T get() {
        if (!isCalculated) {
            synchronized (valueSupplier) {
                if (!isCalculated) {
                    value = valueSupplier.get();
                    isCalculated = true;
                }
            }
        }
        return value;
    }

    /**
     * Returns true if the value has been calculated, else false.
     */
    public boolean isCalculated() {
        return isCalculated;
    }
}
