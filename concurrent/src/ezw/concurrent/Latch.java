package ezw.concurrent;

import java.util.concurrent.CountDownLatch;

public class Latch {
    private final CountDownLatch countDownLatch;

    /**
     * Creates a latch.
     */
    public Latch() {
        countDownLatch = new CountDownLatch(1);
    }

    public void await() throws InterruptedException {
        countDownLatch.await();
    }

    public void release() {
        countDownLatch.countDown();
    }

    public boolean isReleased() {
        return countDownLatch.getCount() == 0;
    }
}
