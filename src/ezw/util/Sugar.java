package ezw.util;

import ezw.util.function.UnsafeConsumer;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Various syntax sugar utilities.
 */
public abstract class Sugar {

    private Sugar() {}

    /**
     * Returns a supplier calling the callable and returning the result, or a function result if thrown an exception.
     * @param callable The callable.
     * @param onException A function returning a result on the callable exception.
     * @param <T> The callable return type.
     * @return A supplier of the callable result if returned, or else the function result.
     */
    public static <T> Supplier<T> orElse(Callable<T> callable, Function<Exception, T> onException) {
        Objects.requireNonNull(callable, "Callable is null.");
        Objects.requireNonNull(onException, "Exception function is null.");
        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                return onException.apply(e);
            }
        };
    }

    /**
     * Returns a supplier calling the callable and testing its result for success.
     * @param callable The callable.
     * @param success A predicate testing the result for success.
     * @param <T> The callable return type.
     * @return A supplier of the success test result. False might mean a false test result, failure of the callable, or
     * failure of the predicate.
     */
    public static <T> Supplier<Boolean> success(Callable<T> callable, Predicate<T> success) {
        Objects.requireNonNull(callable, "Callable is null.");
        Objects.requireNonNull(success, "Success predicate is null.");
        return orElse(() -> success.test(callable.call()), e -> false);
    }

    /**
     * Repeats a runnable call.
     * @param times The number of times to repeat.
     * @param runnable The runnable.
     */
    public static void repeat(int times, Runnable runnable) {
        Objects.requireNonNull(runnable, "Runnable is null.");
        for (int i = 0; i < times; i++) {
            runnable.run();
        }
    }

    /**
     * Produces and accepts values into the consumer as long as they pass the predicate.
     * @param callable The callable producing the values.
     * @param consumer The values consumer.
     * @param predicate The predicate testing the values.
     * @param <T> The values type.
     * @throws Exception Any exception thrown by the implementations.
     */
    public static <T> void acceptWhile(Callable<T> callable, UnsafeConsumer<T> consumer, Predicate<T> predicate)
            throws Exception {
        Objects.requireNonNull(consumer, "Consumer is null.");
        Objects.requireNonNull(predicate, "Predicate is null.");
        T value = Objects.requireNonNull(callable, "Callable is null.").call();
        while (predicate.test(value)) {
            consumer.accept(value);
            value = callable.call();
        }
    }

    /**
     * Wraps a callable implementation in a Supplier throwing sneaky.
     */
    public static <T> Supplier<T> toSupplier(Callable<T> callable) {
        Objects.requireNonNull(callable, "Callable is null.");
        return () -> {
            try {
                return callable.call();
            } catch (Exception e) {
                throw sneaky(e);
            }
        };
    }

    /**
     * Returns the exception as is if runtime exception, else as undeclared.
     */
    public static RuntimeException sneaky(Exception e) {
        return e instanceof RuntimeException ? (RuntimeException) e : new UndeclaredThrowableException(e);
    }

    /**
     * Validates that the array is not null or empty.
     * @param objects The array.
     * @param <T> The members type.
     * @return The array.
     * @throws NullPointerException If the array is null.
     * @throws IllegalArgumentException If the array is empty.
     */
    public static <T> T[] requireNonEmpty(T[] objects) {
        if (Objects.requireNonNull(objects, "Array is null.").length == 0)
            throw new IllegalArgumentException("Array is empty.");
        return objects;
    }

    /**
     * Validates that the list is not null or empty.
     * @param objects The list.
     * @param <T> The members type.
     * @return The list.
     * @throws NullPointerException If the list is null.
     * @throws IllegalArgumentException If the list is empty.
     */
    public static <T> List<T> requireNonEmpty(List<T> objects) {
        if (Objects.requireNonNull(objects, "List is null.").isEmpty())
            throw new IllegalArgumentException("List is empty.");
        return objects;
    }

    /**
     * Validates that the array and every one of its members is not null.
     * @param objects The array.
     * @param <T> The members type.
     * @return The array.
     * @throws NullPointerException If the array or any of its members is null.
     */
    public static <T> T[] requireNoneNull(T[] objects) {
        Arrays.stream(Objects.requireNonNull(objects, "Array is null.")).forEach(
                o -> Objects.requireNonNull(o, "Array contains a null reference."));
        return objects;
    }

    /**
     * Validates that the iterable and every one of its members is not null.
     * @param objects The iterable.
     * @param <I> The members type.
     * @return The iterable.
     * @throws NullPointerException If the iterable or any of its members is null.
     */
    public static <I extends Iterable<?>> I requireNoneNull(I objects) {
        Objects.requireNonNull(objects, "Iterable is null.").forEach(
                o -> Objects.requireNonNull(o, "Iterable contains a null reference."));
        return objects;
    }

    /**
     * Validates that the array is not null or empty, and every one of its members is not null. Equivalent to:
     * <pre>
     * requireNonEmpty(requireNoneNull(objects));
     * </pre>
     * @param objects The array.
     * @param <T> The members type.
     * @return The array.
     */
    public static <T> T[] requireFull(T[] objects) {
        return requireNonEmpty(requireNoneNull(objects));
    }

    /**
     * Validates that the list is not null or empty, and every one of its members is not null. Equivalent to:
     * <pre>
     * requireNonEmpty(requireNoneNull(objects));
     * </pre>
     * @param objects The list.
     * @param <T> The members type.
     * @return The list.
     */
    public static <T> List<T> requireFull(List<T> objects) {
        return requireNonEmpty(requireNoneNull(objects));
    }

    /**
     * Returns optional of the first non-null object.
     */
    @SafeVarargs
    public static <T> Optional<T> firstNonNull(T... objects) {
        return Arrays.stream(Objects.requireNonNull(objects, "Array is null.")).filter(Objects::nonNull).findFirst();
    }

    /**
     * Returns the first member of the array. Throws appropriate exceptions if the array is null or empty.
     */
    public static <T> T first(T[] objects) {
        return requireNonEmpty(objects)[0];
    }

    /**
     * Returns the last member of the array. Throws appropriate exceptions if the array is null or empty.
     */
    public static <T> T last(T[] objects) {
        return requireNonEmpty(objects)[objects.length - 1];
    }

    /**
     * Returns the first member of the list. Throws appropriate exceptions if the list is null or empty.
     */
    public static <T> T first(List<T> objects) {
        return requireNonEmpty(objects).get(0);
    }

    /**
     * Returns the last member of the list. Throws appropriate exceptions if the list is null or empty.
     */
    public static <T> T last(List<T> objects) {
        return requireNonEmpty(objects).get(objects.size() - 1);
    }

    /**
     * Returns a set of all members of the collection that are instances of a certain type.
     * @param objects An objects collection.
     * @param type A class.
     * @param <T> The type to cast the found members to. Must be assignable from <code>type</code> - not validated.
     * @return A new set of the matching members.
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> instancesOf(Collection<?> objects, Class<?> type) {
        Objects.requireNonNull(type, "Type is null.");
        return Objects.requireNonNull(objects, "Collection is null.").stream().filter(type::isInstance)
                .map(o -> (T) o).collect(Collectors.toSet());
    }

    /**
     * Returns a flat union array of the objects passed. That is, for any member being an array, an iterable or a stream
     * itself, the inner members are added to the flat union. The order of the items is preserved as in the members.
     * @param objects An array of objects.
     * @return A flat union array of the objects passed.
     */
    public static Object[] flat(Object... objects) {
        return Arrays.stream(Objects.requireNonNull(objects, "Array is null.")).flatMap(o -> {
            if (o instanceof Object[])
                return Stream.of((Object[]) o);
            else if (o instanceof Iterable)
                return StreamSupport.stream(((Iterable<?>) o).spliterator(), false);
            else if (o instanceof Stream)
                return (Stream<?>) o;
            else
                return Stream.of(o);
        }).toArray();
    }

    /**
     * Constructs a strings array containing the result of <code>toString</code> for each non-null array member.
     * @param array The array.
     * @param <T> The members type.
     * @return The strings array.
     */
    public static <T> String[] toStrings(T[] array) {
        return Arrays.stream(Objects.requireNonNull(array, "Array is null.")).filter(Objects::nonNull)
                .map(Objects::toString).toArray(String[]::new);
    }

    /**
     * Removes all instances of all substrings listed from the text.
     * @param text The text.
     * @param substrings The substrings to remove.
     * @return The resulting string.
     */
    public static String remove(String text, String... substrings) {
        return replace(text, toStrings(Arrays.stream(requireNoneNull(substrings)).flatMap(s -> Stream.of(s, ""))
                .toArray()));
    }

    /**
     * Performs an ordered set of replacements on the text, replacing the first string with the second, the third with
     * the forth and so on. Each replacement is repeated as long as instances of the target string still exist in the
     * text, in order to support repetitive patterns.
     * @param text The text.
     * @param replacements The target and replacement substrings (must be even).
     * @return The resulting string.
     */
    public static String replace(String text, String... replacements) {
        if (requireNoneNull(replacements).length % 2 != 0)
            throw new IllegalArgumentException("The replacements array length must be even.");
        for (int i = 0; i < replacements.length; i += 2) {
            String target = replacements[i];
            String replacement = replacements[i + 1];
            if (target.equals(replacement))
                continue;
            while (text.contains(target)) {
                text = text.replace(target, replacement);
            }
        }
        return text;
    }
}
