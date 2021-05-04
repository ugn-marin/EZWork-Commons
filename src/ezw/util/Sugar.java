package ezw.util;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

/**
 * Various syntax sugar utilities.
 */
public abstract class Sugar {

    private Sugar() {}

    /**
     * Calls the callable and returns the result, or the default value if thrown an exception.
     * @param callable The callable.
     * @param defaultValue The default value.
     * @param <T> The callable return type.
     * @return The callable result, or default value if an exception was thrown.
     */
    public static <T> T orElse(Callable<T> callable, T defaultValue) {
        try {
            return callable.call();
        } catch (Exception ignore) {
            return defaultValue;
        }
    }

    /**
     * Collections utilities.
     */
    public static abstract class Collections {

        private Collections() {}

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
                    o -> Objects.requireNonNull(o, "Objects contain a null reference."));
            return objects;
        }

        /**
         * Returns the first member of the array. Throws appropriate exceptions if the array is null or empty.
         */
        public static <T> T first(T[] objects) {
            return objects[0];
        }

        /**
         * Returns the last member of the array. Throws appropriate exceptions if the array is null or empty.
         */
        public static <T> T last(T[] objects) {
            return objects[objects.length - 1];
        }

        /**
         * Returns the first member of the list. Throws appropriate exceptions if the list is null or empty.
         */
        public static <T> T first(List<T> objects) {
            return objects.get(0);
        }

        /**
         * Returns the last member of the list. Throws appropriate exceptions if the list is null or empty.
         */
        public static <T> T last(List<T> objects) {
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
            return objects.stream().filter(type::isInstance).map(o -> (T) o).collect(Collectors.toSet());
        }
    }

    /**
     * IO utilities.
     */
    public static abstract class IO {
        /**
         * The OS line separator.
         */
        public static final String BR = System.getProperty("line.separator");
        /**
         * The OS file separator.
         */
        public static final String DIR = System.getProperty("file.separator");

        private IO() {}
    }
}
