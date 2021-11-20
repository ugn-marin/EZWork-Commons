package ezw.function;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

public class ReducerTest {

    @Test
    void max() {
        Assertions.assertEquals(100, Reducer.<Integer>max().apply(List.of(22, 3, 56, 82, -94, 100, 30)));
        Assertions.assertEquals("000", Reducer.max(Comparator.comparingInt(String::length)).apply(List.of(
                "22", "3", "56", "82", "000", "30")));
    }

    @Test
    void min() {
        Assertions.assertEquals(-94, Reducer.<Integer>min().apply(List.of(22, 3, 56, 82, -94, 100, 30)));
        Assertions.assertEquals("3", Reducer.min(Comparator.comparingInt(String::length)).apply(List.of(
                "22", "3", "56", "82", "000", "30")));
    }

    @Test
    void andThen() {
        Assertions.assertEquals("3", Reducer.<Integer>max().andThen(Object::toString).apply(List.of(1, 2, 3)));
        Assertions.assertEquals(10, Reducer.<Integer>min().andThen(i -> i * 10).apply(List.of(1, 2, 3)));
    }

    @Test
    void orElse() {
        Assertions.assertEquals(8, Reducer.<Integer>max().orElse(() -> 8).apply(List.of()));
        Assertions.assertEquals(8, Reducer.<Integer>min().orElse(() -> 8).apply(null));
    }

    @Test
    void orElseNull() {
        Assertions.assertNull(Reducer.first().apply(List.of()));
        Assertions.assertNull(Reducer.last().apply(null));
        Assertions.assertNull(Reducer.max().orElseNull().apply(List.of()));
        Assertions.assertNull(Reducer.min().orElseNull().apply(null));
    }
}
