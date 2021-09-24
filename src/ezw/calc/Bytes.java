package ezw.calc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Various bytes utilities.
 */
public abstract class Bytes {

    private Bytes() {}

    /**
     * Compresses bytes using a GZIP stream with a default buffer size.
     * @param bytes A bytes array.
     * @return A zipped bytes array.
     * @throws IOException If an I/O error occurred.
     */
    public static byte[] zip(byte[] bytes) throws IOException {
        try (var out = new ByteArrayOutputStream()) {
            try (var gzip = new GZIPOutputStream(out)) {
                gzip.write(bytes);
            }
            return out.toByteArray();
        }
    }

    /**
     * Decompresses bytes using a GZIP stream with a default buffer size.
     * @param bytes A bytes array.
     * @return An unzipped bytes array.
     * @throws IOException If an I/O error occurred.
     */
    public static byte[] unzip(byte[] bytes) throws IOException {
        try (var in = new ByteArrayInputStream(bytes)) {
            try (var gzip = new GZIPInputStream(in)) {
                return gzip.readAllBytes();
            }
        }
    }
}
