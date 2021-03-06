package ezw.calc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CalcTest {

    @Test
    void timeUnitsDescription() {
        Assertions.assertEquals("1000 milliseconds", Units.Time.describe(Units.Time.SECOND));
        Assertions.assertEquals("60.0 seconds", Units.Time.describe(Units.Time.MINUTE));
        Assertions.assertEquals("60.0 minutes", Units.Time.describe(Units.Time.HOUR));
        Assertions.assertEquals("24.0 hours", Units.Time.describe(Units.Time.DAY));
        Assertions.assertEquals("7.0 days", Units.Time.describe(Units.Time.WEEK));

        Assertions.assertEquals("0 milliseconds", Units.Time.describe(0));
        Assertions.assertEquals("400 milliseconds", Units.Time.describe(400));
        Assertions.assertEquals("-25 milliseconds", Units.Time.describe(-25));
        Assertions.assertEquals("1999 milliseconds", Units.Time.describe(1999));
        Assertions.assertEquals("4.0 seconds", Units.Time.describe(4000));
        Assertions.assertEquals("2.12 seconds", Units.Time.describe(2123));
        Assertions.assertEquals("2.13 seconds", Units.Time.describe(2125));
        Assertions.assertEquals("80.5 seconds", Units.Time.describe(80500));
        Assertions.assertEquals("-90.5 seconds", Units.Time.describe(-90500));
        Assertions.assertEquals("120.0 seconds", Units.Time.describe(120000));
        Assertions.assertEquals("2.1 minutes", Units.Time.describe(126000));
        Assertions.assertEquals("16.32 minutes", Units.Time.describe(978990));
        Assertions.assertEquals("120.0 minutes", Units.Time.describe(60000 * 120));
        Assertions.assertEquals("2.1 hours", Units.Time.describe(60000 * 126));
        Assertions.assertEquals("72.0 hours", Units.Time.describe(60000 * 60 * 72));
        Assertions.assertEquals("3.0 days", Units.Time.describe(60001 * 60 * 72));
        Assertions.assertEquals("21.0 days", Units.Time.describe(60000 * 60 * 72 * 7));
        Assertions.assertEquals("3.0 weeks", Units.Time.describe(60001 * 60 * 72 * 7));
        Assertions.assertEquals("300.0 weeks", Units.Time.describe(60000L * 60 * 72 * 7 * 100));
        Assertions.assertEquals("-1001.0 weeks", Units.Time.describe(-60000L * 60 * 24 * 7 * 1001));
    }

    @Test
    void timeUnitsSince() throws InterruptedException {
        long startMillis = System.currentTimeMillis();
        Thread.sleep(200);
        System.out.println(Units.Time.describeSince(startMillis));

        long startNano = System.nanoTime();
        Thread.sleep(200);
        System.out.println(Units.Time.describeSinceNano(startNano));
    }

    @Test
    void sizeUnitsDescription() {
        Assertions.assertEquals("1024 bytes", Units.Size.describe(Units.Size.KB));
        Assertions.assertEquals("1024.0 KB", Units.Size.describe(Units.Size.MB));
        Assertions.assertEquals("1024.0 MB", Units.Size.describe(Units.Size.GB));
        Assertions.assertEquals("1024.0 GB", Units.Size.describe(Units.Size.TB));
        Assertions.assertEquals("1024.0 TB", Units.Size.describe(Units.Size.PB));

        Assertions.assertEquals("0 bytes", Units.Size.describe(0));
        Assertions.assertEquals("500 bytes", Units.Size.describe(500));
        Assertions.assertEquals("-100 bytes", Units.Size.describe(-100));
        Assertions.assertEquals("1.0 KB", Units.Size.describe(1025));
        Assertions.assertEquals("7.81 KB", Units.Size.describe(8000));
        Assertions.assertEquals("-7.81 KB", Units.Size.describe(-8000));
        Assertions.assertEquals("200.0 MB", Units.Size.describe(Units.Size.MB * 200));
        Assertions.assertEquals("300.0 GB", Units.Size.describe(Units.Size.GB * 300));
        Assertions.assertEquals("400.0 TB", Units.Size.describe(Units.Size.TB * 400));
        Assertions.assertEquals("500.0 PB", Units.Size.describe(Units.Size.PB * 500));
        Assertions.assertEquals("-5000.0 PB", Units.Size.describe(Units.Size.PB * -5000));
    }

    @Test
    void zip() throws IOException {
        String text = "There's nothing special about this text, but repeating it will make it easy to zip.".repeat(999);
        byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
        byte[] zipped = Bytes.zip(utf8);
        System.out.printf("Zipped %s into %s%n", Units.Size.describe(utf8.length), Units.Size.describe(zipped.length));
        Assertions.assertTrue(zipped.length < utf8.length);
        byte[] unzipped = Bytes.unzip(zipped);
        Assertions.assertEquals(text, new String(unzipped, StandardCharsets.UTF_8));
    }
}
