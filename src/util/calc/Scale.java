package util.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

/**
 * A function modifying double values to a defined scale.
 */
public class Scale implements Function<Double, Double> {
    private static final Scale defaultScale = new Scale();
    private final int scale;

    /**
     * Constructs a scale function with a default scale of 2.
     */
    public Scale() {
        this(2);
    }

    /**
     * Constructs a scale function.
     * @param scale The target scale.
     */
    public Scale(int scale) {
        this.scale = scale;
    }

    /**
     * Returns the default scale function instance with a scale of 2.
     */
    public static Scale getDefault() {
        return defaultScale;
    }

    /**
     * Applies the scale.
     * @param n The value.
     * @return If <code>n</code> is null, infinite or NaN, returned as is. Else, returns a new double with the value of
     * <code>n</code> with the defined scale.
     */
    @Override
    public Double apply(Double n) {
        return n == null || n.isInfinite() || n.isNaN() ? n : (Double) BigDecimal.valueOf(n).setScale(scale,
                RoundingMode.HALF_UP).doubleValue();
    }
}
