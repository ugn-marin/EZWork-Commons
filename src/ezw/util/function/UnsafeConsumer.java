package ezw.util.function;

import ezw.util.Sugar;

import java.util.function.Consumer;

/**
 * A functional consumer allowing exceptions, and is convertible to Consumer.
 * @param <I> The input type.
 */
@FunctionalInterface
public interface UnsafeConsumer<I> {

    void accept(I item) throws Exception;

    /**
     * Wraps this consumer implementation in a Consumer throwing sneaky.
     */
    default Consumer<I> toConsumer() {
        return item -> {
            try {
                accept(item);
            } catch (Exception e) {
                throw Sugar.sneaky(e);
            }
        };
    }
}
