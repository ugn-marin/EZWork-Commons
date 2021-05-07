package ezw.util;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * A lazy supplier decorator.
 * @param <T> The value type.
 */
public class Lazy<T> implements Supplier<T> {
    private final Supplier<T> valueSupplier;
    private final AtomicBoolean isCalculated = new AtomicBoolean();
    private T value;

    /**
     * Constructs a lazy supplier.
     * @param valueSupplier The value supplier. Will be calculated on the first attempt to get the value. If the
     *                      calculation fails, the value will remain null.
     */
    public Lazy(Supplier<T> valueSupplier) {
        this.valueSupplier = Objects.requireNonNull(valueSupplier, "Value supplier cannot be null.");
    }

    @Override
    public T get() {
        if (!isCalculated.getAndSet(true))
            value = valueSupplier.get();
        return value;
    }
}
