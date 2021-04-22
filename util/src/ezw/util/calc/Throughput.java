package ezw.util.calc;

import java.util.concurrent.atomic.AtomicLong;

public class Throughput {
    private final AtomicLong sizeBytes = new AtomicLong();
    private final AtomicLong timeNano = new AtomicLong();

    public long addSizeBytes(long bytes) {
        return sizeBytes.addAndGet(bytes);
    }

    public long addTimeNano(long nano) {
        return timeNano.addAndGet(nano);
    }

    public void add(Throughput throughput) {
        addSizeBytes(throughput.sizeBytes.get());
        addTimeNano(throughput.timeNano.get());
    }

    public void reset() {
        sizeBytes.set(0);
        timeNano.set(0);
    }

    public double getMBSec() {
        return Scale.getDefault().apply(Units.Size.convertBytesToMB(sizeBytes.get()) /
                Units.Time.convertNanoToSeconds(timeNano.get()));
    }
}
