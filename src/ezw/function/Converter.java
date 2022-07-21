package ezw.function;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A function converting inputs according to their types matching the provided functions map.
 * @param <I> The input type.
 * @param <O> The output type.
 */
public class Converter<I, O> implements Function<I, O> {
    private final Map<Class<? extends I>, Function<? extends I, O>> matches;

    /**
     * Constructs a converter.
     * @param matches The map of functions converting inputs according to their type.
     */
    public Converter(Map<Class<? extends I>, Function<? extends I, O>> matches) {
        this.matches = Objects.requireNonNull(matches, "Matches are null.");
        matches.forEach((type, function) -> {
            Objects.requireNonNull(type, "A match type is null.");
            Objects.requireNonNull(function, "A converting function is null.");
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public O apply(I t) {
        if (t != null) {
            var match = matches.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(t.getClass()))
                    .map(Map.Entry::getValue).findFirst();
            if (match.isPresent())
                return ((Function<I, O>) match.get()).apply(t);
        }
        return null;
    }
}
