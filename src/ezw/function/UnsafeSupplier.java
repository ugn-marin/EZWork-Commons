package ezw.function;

import ezw.Sugar;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A functional supplier allowing exceptions, and is convertible to Supplier and Callable.
 * @param <O> The output type.
 */
@FunctionalInterface
public interface UnsafeSupplier<O> {

    O get() throws Exception;

    /**
     * Wraps this supplier implementation in a Supplier throwing sneaky.
     */
    default Supplier<O> toSupplier() {
        return Sugar.toSupplier(toCallable());
    }

    /**
     * Wraps this supplier implementation in a Callable.
     */
    default Callable<O> toCallable() {
        return this::get;
    }
}
