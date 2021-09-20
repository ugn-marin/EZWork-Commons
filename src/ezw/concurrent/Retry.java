package ezw.concurrent;

import ezw.util.Sugar;

import java.io.InterruptedIOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * A callable retrying on exceptions.
 * @param <O> The output type.
 */
public final class Retry<O> implements Callable<O> {
    private final Callable<O> callable;
    private final int tries;
    private final boolean isInfinite;
    private final Function<Integer, Long> intervalFunction;
    private final BiPredicate<Integer, Exception> continuePredicate;
    private final Function<List<Exception>, Exception> exceptionsReducer;

    private Retry(Callable<O> callable, int tries, Function<Integer, Long> intervalFunction,
                  BiPredicate<Integer, Exception> continuePredicate,
                  Function<List<Exception>, Exception> exceptionsReducer) {
        this.callable = callable;
        this.tries = tries;
        this.isInfinite = tries == Integer.MAX_VALUE;
        this.intervalFunction = intervalFunction;
        this.continuePredicate = continuePredicate;
        this.exceptionsReducer = exceptionsReducer;
    }

    /**
     * Constructs a builder of a retry.
     * @param callable The callable.
     * @param tries The number of tries before throwing the exception.
     * @param <O> The output type.
     * @return The builder.
     */
    public static <O> Builder<O> of(Callable<O> callable, int tries) {
        return new Builder<>(callable, tries);
    }

    /**
     * Constructs a builder of an indefinite retry.
     * @param callable The callable.
     * @param <O> The output type.
     * @return The builder.
     */
    public static <O> Builder<O> indefinitely(Callable<O> callable) {
        return of(callable, Integer.MAX_VALUE);
    }

    /**
     * Returns a blacklist-based continue predicate: Continues while the exception is not of the specified types.
     */
    @SafeVarargs
    public static BiPredicate<Integer, Exception> blacklist(Class<? extends Exception>... types) {
        return (t, e) -> !Sugar.instanceOfAny(e, types);
    }

    /**
     * Returns a whitelist-based continue predicate: Continues while the exception is of the specified types.
     */
    @SafeVarargs
    public static BiPredicate<Integer, Exception> whitelist(Class<? extends Exception>... types) {
        return (t, e) -> Sugar.instanceOfAny(e, types);
    }

    @Override
    public O call() throws Exception {
        List<Exception> exceptions = new ArrayList<>(isInfinite ? 1 : tries);
        for (int retry = 0; retry < tries; retry++) {
            if (retry > 0)
                Thread.sleep(intervalFunction.apply(retry));
            try {
                return callable.call();
            } catch (Exception e) {
                if (!continuePredicate.test(retry, e))
                    throw e;
                if (!isInfinite || exceptions.isEmpty())
                    exceptions.add(e);
            }
        }
        throw exceptionsReducer.apply(exceptions);
    }

    /**
     * A retry builder.
     * @param <O> The retry output type.
     */
    public static final class Builder<O> {
        private final Callable<O> callable;
        private final int tries;
        private Function<Integer, Long> intervalFunction;
        private BiPredicate<Integer, Exception> continuePredicate;
        private Function<List<Exception>, Exception> exceptionsReducer;

        private Builder(Callable<O> callable, int tries) {
            this.callable = Objects.requireNonNull(callable, "Callable is null.");
            this.tries = Sugar.requireRange(tries, 1, null);
        }

        /**
         * Sets the function calculating the sleeping interval between each retry. The default logic is no interval
         * (constant zero).
         * @param intervalFunction A function getting the retry number, returning the number of milliseconds to sleep.
         * @return This builder.
         */
        public Builder<O> interval(Function<Integer, Long> intervalFunction) {
            this.intervalFunction = intervalFunction;
            return this;
        }

        /**
         * Sets a constant sleeping interval between retries.
         * @param interval The interval in milliseconds.
         * @return This builder.
         */
        public Builder<O> intervalConstant(long interval) {
            return interval(retry -> interval);
        }

        /**
         * Sets the function calculating the sleeping interval between each retry as a growing progression, starting
         * with <code>interval</code> and growing by the same interval with each retry until reaching <code>max</code>.
         * In other words:<br>
         * <i>(1 * interval, 2 * interval, ..., max)</i>
         * @param interval The initial interval.
         * @param max The maximum interval the progression will reach.
         * @return This builder.
         */
        public Builder<O> intervalProgression(long interval, long max) {
            return interval(retry -> Math.min(interval * retry, max));
        }

        /**
         * Sets the predicate deciding whether to continue on an exception. Can be based on a blacklist or a whitelist
         * of exception types (with the help of the <code>blacklist</code> and <code>whitelist</code> methods
         * accordingly), or any other logic. The default logic is a blacklist of interruption exceptions.
         * @param continuePredicate A bi-predicate getting the retry number and the exception, returning true if retries
         *                          should continue, else false.
         * @return This builder.
         */
        public Builder<O> continueWhile(BiPredicate<Integer, Exception> continuePredicate) {
            this.continuePredicate = continuePredicate;
            return this;
        }

        /**
         * Sets the function choosing or constructing the exception based on the list of the exceptions received on all
         * retries. The default logic is throwing the last exception.
         * @param exceptionsReducer A function getting the retries exceptions list, returning the exception to throw.
         * @return This builder.
         */
        public Builder<O> reduce(Function<List<Exception>, Exception> exceptionsReducer) {
            this.exceptionsReducer = exceptionsReducer;
            return this;
        }

        /**
         * Builds the retry.
         * @return The retry.
         */
        public Retry<O> build() {
            return new Retry<>(callable, tries, Objects.requireNonNullElse(intervalFunction, t -> 0L),
                    Objects.requireNonNullElse(continuePredicate, blacklist(InterruptedException.class,
                            InterruptedRuntimeException.class, ClosedByInterruptException.class,
                            InterruptedIOException.class)), Objects.requireNonNullElse(exceptionsReducer, Sugar::last));
        }
    }
}
