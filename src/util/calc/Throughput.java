package util.calc;

import java.util.concurrent.atomic.AtomicLong;

/**
 * An accumulative, thread safe throughput counter, gathering size (bytes) and time (nanoseconds) for calculating the
 * average MB/sec.
 */
public class Throughput {
    private final AtomicLong sizeBytes = new AtomicLong();
    private final AtomicLong timeNano = new AtomicLong();

    /**
     * Adds a number of bytes to the total size count.
     * @param bytes The bytes delta.
     * @return The new total bytes size.
     */
    public long addSizeBytes(long bytes) {
        return sizeBytes.addAndGet(bytes);
    }

    /**
     * Adds a number of nanoseconds to the total time count.
     * @param nano The nanoseconds delta.
     * @return The new total nanoseconds time.
     */
    public long addTimeNano(long nano) {
        return timeNano.addAndGet(nano);
    }

    /**
     * Adds a sub-throughput to include in the average.
     */
    public void add(Throughput throughput) {
        addSizeBytes(throughput.sizeBytes.get());
        addTimeNano(throughput.timeNano.get());
    }

    /**
     * Resets all gathered info.
     */
    public void reset() {
        sizeBytes.set(0);
        timeNano.set(0);
    }

    /**
     * Returns the average MB/sec throughput so far, with the default {@link Scale} applied.
     */
    public double getMBSec() {
        return Scale.getDefault().apply(Units.Size.convertBytesToMB(sizeBytes.get()) /
                Units.Time.convertNanoToSeconds(timeNano.get()));
    }
}
