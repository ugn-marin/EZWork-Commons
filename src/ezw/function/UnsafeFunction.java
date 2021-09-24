package ezw.function;

import ezw.Sugar;

import java.util.function.Function;

/**
 * A functional function allowing exceptions, and is convertible to Function.
 * @param <I> The input type.
 * @param <O> The output type.
 */
@FunctionalInterface
public interface UnsafeFunction<I, O> {

    O apply(I item) throws Exception;

    /**
     * Wraps this function implementation in a Function throwing sneaky.
     */
    default Function<I, O> toFunction() {
        return item -> {
            try {
                return apply(item);
            } catch (Exception e) {
                throw Sugar.sneaky(e);
            }
        };
    }
}
