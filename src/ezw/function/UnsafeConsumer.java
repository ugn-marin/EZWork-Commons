package ezw.function;

import ezw.Sugar;

import java.util.function.Consumer;

/**
 * A functional consumer allowing exceptions, and is convertible to Consumer.
 * @param <I> The input type.
 */
@FunctionalInterface
public interface UnsafeConsumer<I> {

    void accept(I t) throws Exception;

    /**
     * Wraps this consumer implementation in a Consumer throwing sneaky.
     */
    default Consumer<I> toConsumer() {
        return t -> {
            try {
                accept(t);
            } catch (Exception e) {
                throw Sugar.sneaky(e);
            }
        };
    }
}
