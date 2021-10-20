package ezw.function;

import ezw.Sugar;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A function reducing a list of items to a single item.
 * @param <O> The items type.
 */
public interface Reducer<O> extends Function<List<O>, O> {

    /**
     * Returns a reducer returning the first item of full lists, or else null. Equivalent to <code>Sugar::first</code>,
     * if the input is guaranteed to be full (non-null and non-empty).
     * @param <O> The items type.
     * @return The reducer.
     */
    static <O> Reducer<O> first() {
        return orElse(Sugar::first, () -> null);
    }

    /**
     * Returns a reducer returning the last item of full lists, or else null.  Equivalent to <code>Sugar::last</code>,
     * if the input is guaranteed to be full (non-null and non-empty).
     * @param <O> The items type.
     * @return The reducer.
     */
    static <O> Reducer<O> last() {
        return orElse(Sugar::last, () -> null);
    }

    /**
     * Returns a decorator applying the reducer on full lists, or returning the supplier result on null or empty lists.
     * @param reducer The reducer.
     * @param onNullOrEmpty The supplier to run on null or empty lists.
     * @param <O> The items type.
     * @return The reducer decorator.
     */
    static <O> Reducer<O> orElse(Reducer<O> reducer, Supplier<O> onNullOrEmpty) {
        return items -> items == null || items.isEmpty() ? onNullOrEmpty.get() : reducer.apply(items);
    }
}
