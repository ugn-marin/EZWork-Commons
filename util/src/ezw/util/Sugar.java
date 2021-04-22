package ezw.util;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Sugar {

    private Sugar() {}

    public static abstract class Collections {

        private Collections() {}

        public static <T> T[] requireNoneNull(T[] objects) {
            Arrays.stream(Objects.requireNonNull(objects, "Array is null.")).forEach(
                    o -> Objects.requireNonNull(o, "Array contains a null reference."));
            return objects;
        }

        public static <I extends Iterable<?>> I requireNoneNull(I objects) {
            Objects.requireNonNull(objects, "Iterable is null.").forEach(
                    o -> Objects.requireNonNull(o, "Objects contain a null reference."));
            return objects;
        }

        public static <T> T first(T[] objects) {
            return objects[0];
        }

        public static <T> T last(T[] objects) {
            return objects[objects.length - 1];
        }

        public static <T> T first(List<T> objects) {
            return objects.get(0);
        }

        public static <T> T last(List<T> objects) {
            return objects.get(objects.size() - 1);
        }

        @SuppressWarnings("unchecked")
        public static <T> Set<T> instancesOf(Collection<?> objects, Class<?> type) {
            return objects.stream().filter(type::isInstance).map(o -> (T) o).collect(Collectors.toSet());
        }
    }

    public static abstract class IO {
        /**
         * OS line separator.
         */
        public static final String BR = System.getProperty("line.separator");
        /**
         * OS file separator.
         */
        public static final String DIR = System.getProperty("file.separator");

        private IO() {}
    }
}
