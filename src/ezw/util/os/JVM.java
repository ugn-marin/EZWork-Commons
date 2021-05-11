package ezw.util.os;

import ezw.util.Sugar;

import java.nio.file.Path;
import java.util.List;

/**
 * A JVM command line executor, getting a main class from the classpath, and running it in a new process using the same
 * classpath as the current JVM, with optional parameters and arguments.
 */
public class JVM extends CommandLine {

    /**
     * Constructs a JVM executor.
     * @param mainClass The main class of the new JVM.
     * @param args Optional main arguments.
     */
    public JVM(Class<?> mainClass, String... args) {
        this(true, null, mainClass, args);
    }

    /**
     * Constructs a JVM executor.
     * @param collectOutput If true, the standard output lines are collected, else ignored. Default is true.
     * @param jvmParameters Optional JVM parameters to include in the command line.
     * @param mainClass The main class of the new JVM.
     * @param args Optional main arguments.
     */
    public JVM(boolean collectOutput, List<String> jvmParameters, Class<?> mainClass, String... args) {
        super(collectOutput, Sugar.flat(Path.of(System.getProperty("sun.boot.library.path"), "java").toString(),
                jvmParameters, "-cp", System.getProperty("java.class.path"), mainClass.getName(), args));
    }
}
