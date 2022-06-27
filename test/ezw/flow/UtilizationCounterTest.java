package ezw.flow;

import ezw.concurrent.Concurrent;
import ezw.function.Reducer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UtilizationCounterTest {
    private static final double delta = 0.03;

    @Test
    void flow1() throws Exception {
        var counter = new UtilizationCounter(3);
        counter.start();
        var f1 = Concurrent.run(() -> {
            counter.busy();
            Thread.sleep(50);
            counter.idle();
            Thread.sleep(350);
            counter.busy();
            Thread.sleep(100);
            counter.idle();
        });
        var f2 = Concurrent.run(() -> {
            counter.busy();
            Thread.sleep(200);
            counter.idle();
            Thread.sleep(100);
            counter.busy();
            Thread.sleep(200);
            counter.idle();
        });
        Concurrent.getAll(Reducer.suppressor(), f1, f2);
        double middle = counter.getAverageUtilization();
        Thread.sleep(100);
        counter.stop();
        Assertions.assertEquals(0.306, counter.getAverageUtilization(), delta);
        Assertions.assertEquals(0.366, middle, delta);
    }

    @Test
    void flow2() throws Exception {
        var counter = new UtilizationCounter(3);
        counter.start();
        Assertions.assertEquals(0, counter.getCurrentUtilization());
        Thread.sleep(100);
        var f1 = Concurrent.run(() -> {
            counter.busy();
            Thread.sleep(500);
            counter.idle();
        });
        var f2 = Concurrent.run(() -> {
            Thread.sleep(100);
            counter.busy();
            Assertions.assertEquals(0.66, counter.getCurrentUtilization(), 0.01);
            Thread.sleep(400);
            counter.idle();
        });
        var f3 = Concurrent.run(() -> {
            Thread.sleep(150);
            counter.busy();
            Assertions.assertEquals(1, counter.getCurrentUtilization());
            Thread.sleep(300);
            counter.idle();
        });
        Concurrent.getAll(Reducer.suppressor(), f1, f2, f3);
        counter.stop();
        Assertions.assertEquals(0.667, counter.getAverageUtilization(), delta);
    }
}
