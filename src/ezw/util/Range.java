package ezw.util;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A range of integers.
 */
public class Range extends Couple<Integer> {

    private Range(int from, int to) {
        super(Integer.class, from, to);
    }

    public static Range of(int from, int to) {
        return new Range(from, to);
    }

    public int getFrom() {
        return getFirst();
    }

    public int getTo() {
        return getSecond();
    }

    public int size() {
        return getTo() - getFrom();
    }

    public void forEach(Consumer<Integer> action) {
        Objects.requireNonNull(action, "Action is null.");
        for (int i = getFrom(); i <= getTo(); i++) {
            action.accept(i);
        }
    }
}
