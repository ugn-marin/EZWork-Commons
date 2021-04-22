package ezw.util.calc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class Scale implements Function<Double, Double> {
    private static final Scale defaultScale = new Scale();
    private final int scale;

    public Scale() {
        this(2);
    }

    public Scale(int scale) {
        this.scale = scale;
    }

    public static Scale getDefault() {
        return defaultScale;
    }

    @Override
    public Double apply(Double n) {
        return n == null || n.isInfinite() || n.isNaN() ? n : (Double) BigDecimal.valueOf(n).setScale(scale,
                RoundingMode.HALF_UP).doubleValue();
    }
}
