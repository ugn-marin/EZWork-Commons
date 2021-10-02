package ezw.runtime;

/**
 * Common environment properties.
 */
public abstract class Environment {
    public static final String userName = System.getProperty("user.name");
    public static final String userHome = System.getProperty("user.home");
    public static final String workingDir = System.getProperty("user.dir");
    public static final String tempDir = System.getProperty("java.io.tmpdir");

    private Environment() {}
}
