package ezw.util;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Various syntax sugar utilities.
 */
public abstract class Sugar {

    /**
     * The OS line separator.
     */
    public static final String BR = System.getProperty("line.separator");
    /**
     * The OS file separator.
     */
    public static final String DIR = System.getProperty("file.separator");

    private Sugar() {}

    /**
     * Returns a supplier calling the callable and returning the result, or a function result if thrown an exception.
     * @param callable The callable.
     * @param onException A function returning a result on the callable exception.
     * @param <T> The callable return type.
     * @return A supplier of the callable result if returned, or else the function result.
     */
    public static <T> Supplier<T> either(Callable<T> callable, Function<Exception, T> onException) {
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
     * Returns the first member of the array. Throws appropriate exceptions if the array is null or empty.
     */
    public static <T> T first(T[] objects) {
        if (Objects.requireNonNull(objects, "Array is null.").length == 0)
            throw new IllegalArgumentException("Array is empty.");
        return objects[0];
    }

    /**
     * Returns the last member of the array. Throws appropriate exceptions if the array is null or empty.
     */
    public static <T> T last(T[] objects) {
        if (Objects.requireNonNull(objects, "Array is null.").length == 0)
            throw new IllegalArgumentException("Array is empty.");
        return objects[objects.length - 1];
    }

    /**
     * Returns the first member of the list. Throws appropriate exceptions if the list is null or empty.
     */
    public static <T> T first(List<T> objects) {
        if (Objects.requireNonNull(objects, "List is null.").isEmpty())
            throw new IllegalArgumentException("List is empty.");
        return objects.get(0);
    }

    /**
     * Returns the last member of the list. Throws appropriate exceptions if the list is null or empty.
     */
    public static <T> T last(List<T> objects) {
        if (Objects.requireNonNull(objects, "List is null.").isEmpty())
            throw new IllegalArgumentException("List is empty.");
        return objects.get(objects.size() - 1);
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
     * Returns a flat union array of the objects passed. That is, for any member being an array or an iterable itself,
     * the inner members are added to the union. The order of the items is preserved.
     * @param objects An array of objects.
     * @return A flat union array of the objects passed.
     */
    public static Object[] flat(Object... objects) {
        List<Object> flat = new ArrayList<>(Objects.requireNonNull(objects, "Array is null.").length);
        for (Object o : objects) {
            if (o instanceof Object[])
                flat.addAll(Arrays.asList((Object[]) o));
            else if (o instanceof Iterable)
                ((Iterable<?>) o).forEach(flat::add);
            else
                flat.add(o);
        }
        return flat.toArray();
    }
}
