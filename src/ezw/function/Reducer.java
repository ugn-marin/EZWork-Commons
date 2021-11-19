package ezw.function;

import ezw.Sugar;

import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A function reducing a list of items to a single item.
 * @param <O> The items type.
 */
public interface Reducer<O> extends Function<List<O>, O> {

    /**
     * Returns a decorator applying reducer on non-empty lists, or returning the supplier result on null or empty lists.
     * @param onNullOrEmpty The supplier to run on null or empty lists.
     * @return The reducer decorator.
     */
    default Reducer<O> orElse(Supplier<O> onNullOrEmpty) {
        return orElse(this, onNullOrEmpty);
    }

    /**
     * Returns a decorator applying reducer on non-empty lists, or returning null on null or empty lists.
     * @return The reducer decorator.
     */
    default Reducer<O> orElseNull() {
        return orElseNull(this);
    }

    /**
     * Returns a decorator applying reducer on non-empty lists, or returning the supplier result on null or empty lists.
     * @param reducer The reducer.
     * @param onNullOrEmpty The supplier to run on null or empty lists.
     * @param <O> The items type.
     * @return The reducer decorator.
     */
    static <O> Reducer<O> orElse(Reducer<O> reducer, Supplier<O> onNullOrEmpty) {
        return items -> items == null || items.isEmpty() ? onNullOrEmpty.get() : reducer.apply(items);
    }

    /**
     * Returns a decorator applying reducer on non-empty lists, or returning null on null or empty lists. Equivalent to:
     * <pre>
     * orElse(reducer, () -> null);
     * </pre>
     * @param reducer The reducer.
     * @param <O> The items type.
     * @return The reducer decorator.
     */
    static <O> Reducer<O> orElseNull(Reducer<O> reducer) {
        return orElse(reducer, () -> null);
    }

    /**
     * Returns a reducer returning the first item of non-empty lists, or else null. Equivalent to:
     * <pre>
     * orElseNull(Sugar::first);
     * </pre>
     * @param <O> The items type.
     * @return The reducer.
     */
    static <O> Reducer<O> first() {
        return orElseNull(Sugar::first);
    }

    /**
     * Returns a reducer returning the last item of non-empty lists, or else null. Equivalent to:
     * <pre>
     * orElseNull(Sugar::last);
     * </pre>
     * @param <O> The items type.
     * @return The reducer.
     */
    static <O> Reducer<O> last() {
        return orElseNull(Sugar::last);
    }

    /**
     * Returns a reducer applying a binary operator on non-empty lists as follows: If only one item in the list, it is
     * returned as is, else applied on by the operator with the second item, and the result is applied on by the
     * operator with subsequent items. In other words, with operator applying on items as (#, #): (((0, 1), 2), 3)...
     * Does not include <i>or else</i> functionality for null or empty lists.
     * @param operator The operator.
     * @param <O> The items type.
     * @return The reducer.
     */
    static <O> Reducer<O> from(BinaryOperator<O> operator) {
        return items -> {
            var iterator = Sugar.requireNonEmpty(items).iterator();
            O result = iterator.next();
            while (iterator.hasNext()) {
                result = operator.apply(result, iterator.next());
            }
            return result;
        };
    }
}
