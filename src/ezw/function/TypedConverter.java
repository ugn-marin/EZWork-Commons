package ezw.function;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * A function converting inputs according to their types matching the provided functions map. Inputs that don't match
 * any of the mapped types can be handled by an optional Else function (by default converter to null). Null inputs are
 * always returned as null.
 * @param <I> The input type.
 * @param <O> The output type.
 */
public class TypedConverter<I, O> implements Function<I, O> {
    private final Map<Class<? extends I>, Function<? extends I, O>> matches;
    private final Function<? extends I, O> orElse;

    /**
     * Constructs a converter.
     * @param matches The map of functions converting inputs according to their type. If none match, converts to null.
     */
    public TypedConverter(Map<Class<? extends I>, Function<? extends I, O>> matches) {
        this(matches, t -> null);
    }

    /**
     * Constructs a converter.
     * @param matches The map of functions converting inputs according to their type.
     * @param orElse The converting function for inputs that don't match any of the mapped types.
     */
    public TypedConverter(Map<Class<? extends I>, Function<? extends I, O>> matches, Function<? extends I, O> orElse) {
        this.matches = Objects.requireNonNull(matches, "Matches are null.");
        this.orElse = Objects.requireNonNull(orElse, "Else function is null.");
        matches.forEach((type, function) -> {
            Objects.requireNonNull(type, "A match type is null.");
            Objects.requireNonNull(function, "A converting function is null.");
        });
    }

    @Override
    public O apply(I t) {
        return t != null ? matches.entrySet().stream().filter(entry -> entry.getKey().isAssignableFrom(t.getClass()))
                .map(Map.Entry::getValue).map(this::cast).findFirst().orElse(cast(orElse)).apply(t) : null;
    }

    @SuppressWarnings("unchecked")
    private Function<I, O> cast(Function<? extends I, O> function) {
        return (Function<I, O>) function;
    }
}
