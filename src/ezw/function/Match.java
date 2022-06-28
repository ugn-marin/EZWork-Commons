package ezw.function;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A conditional consumer, having an acceptance logic for either outputs of the predicate.
 * @param <I> The input type.
 */
public record Match<I>(Predicate<I> predicate, Consumer<I> positive, Consumer<I> negative) implements Consumer<I> {

    /**
     * Constructs a match consumer.
     * @param predicate The predicate to decide the acceptance logic.
     * @param positive The acceptance logic for positive inputs.
     * @param negative The acceptance logic for negative inputs.
     */
    public Match(Predicate<I> predicate, Consumer<I> positive, Consumer<I> negative) {
        this.predicate = Objects.requireNonNull(predicate, "Predicate is null.");
        this.positive = Objects.requireNonNull(positive, "Positive consumer is null.");
        this.negative = Objects.requireNonNull(negative, "Negative consumer is null.");
    }

    @Override
    public void accept(I t) {
        (predicate.test(t) ? positive : negative).accept(t);
    }
}
