package ezw.util.calc;

import ezw.util.Sugar;

import java.util.Arrays;

public abstract class Units {

    private Units() {}

    private interface Unit {

        long getValue();

        long getLimit();

        String name();

        default String describe(double value, Unit[] units) {
            return Arrays.stream(units).filter(unit -> Math.abs(value) <= unit.getLimit()).findFirst()
                    .orElse(Sugar.Collections.last(units)).describe(value);
        }

        default String describe(double value) {
            return String.format("%s %s", Scale.getDefault().apply(value / getValue()), name());
        }

        default double convert(double value, Unit targetUnit) {
            return value * ((double) getValue() / targetUnit.getValue());
        }
    }

    public static abstract class Time {

        enum TimeUnit implements Unit {
            milliseconds(1, 2000),
            seconds(1000, 120),
            minutes(60 * seconds.getValue(), 120),
            hours(60 * minutes.getValue(), 72),
            days(24 * hours.getValue(), 21),
            weeks(7 * days.getValue(), 1000);

            private final long value;
            private final long limit;

            TimeUnit(long value, long limitFactor) {
                this.value = value;
                limit = value * limitFactor;
            }

            @Override
            public long getValue() {
                return value;
            }

            @Override
            public long getLimit() {
                return limit;
            }
        }

        /**
         * One second in milliseconds.
         */
        public static final long SECOND = TimeUnit.seconds.getValue();
        /**
         * One minute in milliseconds.
         */
        public static final long MINUTE = TimeUnit.minutes.getValue();
        /**
         * One hour in milliseconds.
         */
        public static final long HOUR = TimeUnit.hours.getValue();
        /**
         * One day in milliseconds.
         */
        public static final long DAY = TimeUnit.days.getValue();
        /**
         * One week in milliseconds.
         */
        public static final long WEEK = TimeUnit.weeks.getValue();

        private Time() {}

        /**
         * Describes the time in appropriate units.
         * @param millis The time in milliseconds.
         * @return The time description.
         */
        public static String describe(double millis) {
            return TimeUnit.milliseconds.describe(millis, TimeUnit.values());
        }

        /**
         * Describes the time in appropriate units.
         * @param nano The time in nanoseconds.
         * @return The time description.
         */
        public static String describeNano(double nano) {
            return describe(convertNanoToMillis(nano));
        }

        public static long since(long startMillis) {
            return System.currentTimeMillis() - startMillis;
        }

        public static long sinceNano(long startNano) {
            return System.nanoTime() - startNano;
        }

        public static String describeSince(long startMillis) {
            return describe(since(startMillis));
        }

        public static String describeSinceNano(long startNano) {
            return describeNano(sinceNano(startNano));
        }

        public static double convertNanoToMillis(double nano) {
            return nano / 1000000.0;
        }

        public static double convertNanoToSeconds(double nano) {
            return TimeUnit.milliseconds.convert(convertNanoToMillis(nano), TimeUnit.seconds);
        }
    }

    public static abstract class Size {

        enum SizeUnit implements Unit {
            bytes(1, 1024),
            KB(bytes.getLimit(), 1024),
            MB(KB.getLimit(), 1024),
            GB(MB.getLimit(), 1024),
            TB(GB.getLimit(), 1024),
            PB(TB.getLimit(), 1024);

            private final long value;
            private final long limit;

            SizeUnit(long value, long limitFactor) {
                this.value = value;
                limit = value * limitFactor;
            }

            @Override
            public long getValue() {
                return value;
            }

            @Override
            public long getLimit() {
                return limit;
            }
        }

        /**
         * One KB in bytes.
         */
        public static final long KB = SizeUnit.KB.getValue();
        /**
         * One MB in bytes.
         */
        public static final long MB = SizeUnit.MB.getValue();
        /**
         * One GB in bytes.
         */
        public static final long GB = SizeUnit.GB.getValue();
        /**
         * One TB in bytes.
         */
        public static final long TB = SizeUnit.TB.getValue();
        /**
         * One PB in bytes.
         */
        public static final long PB = SizeUnit.PB.getValue();

        private Size() {}

        /**
         * Describes the size in appropriate units.
         * @param bytes The size in bytes.
         * @return The size description.
         */
        public static String describe(double bytes) {
            return SizeUnit.bytes.describe(bytes, SizeUnit.values());
        }

        public static double convertBytesToKB(double bytes) {
            return SizeUnit.bytes.convert(bytes, SizeUnit.KB);
        }

        public static double convertBytesToMB(double bytes) {
            return SizeUnit.bytes.convert(bytes, SizeUnit.MB);
        }

        public static double convertMBToBytes(double MB) {
            return SizeUnit.MB.convert(MB, SizeUnit.bytes);
        }
    }
}
