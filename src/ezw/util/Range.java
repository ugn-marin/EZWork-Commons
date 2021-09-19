package ezw.util;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A range of integers.
 */
public final class Range extends Couple<Integer> {

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

    public boolean isEmpty() {
        return size() == 0;
    }

    public int signum() {
        return Integer.signum(size());
    }

    /**
     * Performs an action for each value in this range, from <code>from</code> (inclusive) to <code>to</code>
     * (exclusive). If the range is empty, does nothing.
     */
    public void forEach(Consumer<Integer> action) {
        Objects.requireNonNull(action, "Action is null.");
        for (int i = getFrom(); i != getTo(); i += signum()) {
            action.accept(i);
        }
    }
}