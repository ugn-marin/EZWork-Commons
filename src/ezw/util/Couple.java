package ezw.util;

import java.util.Objects;

abstract class Couple<T> {
    private final Class<T> type;
    private final T first;
    private final T second;

    Couple(Class<T> type, T first, T second) {
        this.type = type;
        this.first = first;
        this.second = second;
    }

    T getFirst() {
        return first;
    }

    T getSecond() {
        return second;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Couple<?> couple = (Couple<?>) o;
        return type == couple.type && equals((T) couple.first, (T) couple.second);
    }

    /**
     * Indicates whether the provided data equals to the data of the couple.
     */
    public boolean equals(T first, T second) {
        return Objects.equals(this.first, first) && Objects.equals(this.second, second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, first, second);
    }

    @Override
    public String toString() {
        return "[" + first + ", " + second + "]";
    }
}
