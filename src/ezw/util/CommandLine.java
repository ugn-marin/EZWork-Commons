package ezw.util;

import ezw.concurrent.Concurrent;
import ezw.concurrent.Interruptible;
import ezw.util.calc.Units;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * An OS command line process executor. Designed as a simplified ProcessBuilder wrapper, to be used directly or by
 * extending this class.
 */
public class CommandLine implements Callable<CommandLine.CommandLineResult> {

    /**
     * This class represents the result of the command line process execution, like status and output lines.
     */
    public final class CommandLineResult {
        private int exitStatus = -1;
        private final List<CommandLineOutputLine> output;
        private ReentrantLock outputLock;
        private boolean errorPrints = false;
        private long nanoTimeTook;

        CommandLineResult(boolean collectOutput) {
            output = new ArrayList<>(collectOutput ? 1 : 0);
            if (collectOutput)
                outputLock = new ReentrantLock(true);
        }

        /**
         * Returns true if the exit status is 0, and no error prints were collected.
         */
        public boolean isSuccessful() {
            return exitStatus == 0 && !errorPrints;
        }

        /**
         * Returns the process exit status.
         */
        public int getExitStatus() {
            return exitStatus;
        }

        /**
         * Returns the total process execution time in nanoseconds.
         */
        public long getNanoTimeTook() {
            return nanoTimeTook;
        }

        /**
         * Returns the output lines if collected. Else, an empty list.
         */
        public List<CommandLineOutputLine> getOutput() {
            return List.copyOf(output);
        }

        void addOutput(CommandLineOutputLine line) {
            outputLock.lock();
            try {
                output.add(line);
                errorPrints |= line.isError();
                var stream = line.isError() ? err : out;
                if (stream != null)
                    stream.println(line.getLine());
            } finally {
                outputLock.unlock();
            }
        }
    }

    /**
     * A command line output line.
     */
    public static final class CommandLineOutputLine {
        private final Date timestamp = new Date();
        private final String line;
        private final boolean isError;

        CommandLineOutputLine(String line, boolean isError) {
            this.line = line;
            this.isError = isError;
        }

        /**
         * Returns the time the line finished printing.
         */
        public Date getTimestamp() {
            return timestamp;
        }

        /**
         * Returns the line content.
         */
        public String getLine() {
            return line;
        }

        /**
         * Returns true if error output, or an IO exception in outputs interception, else false.
         */
        public boolean isError() {
            return isError;
        }
    }

    protected ProcessBuilder processBuilder;
    private final boolean collectOutput;
    private PrintStream out;
    private PrintStream err;
    protected Process process;
    protected CommandLineResult result;
    protected long startNano;

    /**
     * Constructs a command line.
     * @param command The command.
     */
    public CommandLine(Object... command) {
        this(true, command);
    }

    /**
     * Constructs a command line.
     * @param collectOutput If true, the standard output lines are collected, else ignored. Default is true.
     * @param command The command.
     */
    public CommandLine(boolean collectOutput, Object... command) {
        var commandList = Arrays.stream(Objects.requireNonNull(command, "No command received."))
                .filter(Objects::nonNull).map(Object::toString).collect(Collectors.toList());
        if (commandList.isEmpty())
            throw new IllegalArgumentException("No command received.");
        processBuilder = new ProcessBuilder(commandList);
        this.collectOutput = collectOutput;
        if (collectOutput)
            setOutputStreams(System.out, System.err);
    }

    /**
     * Turns off printing of the output lines if collected. Equivalent to:<br><code><pre>
     * setOutputStreams(null, null);</pre></code>
     */
    public void setNoPrints() {
        setOutputStreams(null, null);
    }

    /**
     * Sets the output streams printing the output lines collected. If a null stream is passed, the lines of the
     * according stream will not be printed. In any case the streams do not affect the lines collection, unless printing
     * throws an exception. The streams can be switched during the process runtime. The default streams are the standard
     * system output and error streams. If the process has finished, or command line is not set to collect output, this
     * method has no effect.
     */
    public void setOutputStreams(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    /**
     * Sets the working directory of the process. Has no effect if already started.
     * @param directory The working directory of the process.
     */
    public void setDirectory(File directory) {
        processBuilder.directory(directory);
    }

    /**
     * Adds an environment entry to the process, in addition to the environment it inherits from the current process.
     * Has no effect if already started.
     * @param key The key.
     * @param value The value.
     */
    public void addEnvironment(String key, String value) {
        processBuilder.environment().put(key, value);
    }

    /**
     * Adds environment entries to the process, in addition to the environment it inherits from the current process. Has
     * no effect if already started.
     * @param environment The environment entries to add.
     */
    public void addEnvironment(Map<String, String> environment) {
        processBuilder.environment().putAll(Objects.requireNonNull(environment, "Environment map is null."));
    }

    @Override
    public CommandLineResult call() throws IOException, InterruptedException, ExecutionException {
        Interruptible.validateInterrupted();
        result = new CommandLineResult(collectOutput);
        startNano = System.nanoTime();
        process = processBuilder.start();
        if (collectOutput) {
            ExecutorService outputReadingPool = Executors.newFixedThreadPool(2);
            try {
                Concurrent.getAll(outputReadingPool.submit(getOutputReader(false)),
                        outputReadingPool.submit(getOutputReader(true)));
            } finally {
                outputReadingPool.shutdown();
            }
        }
        result.exitStatus = process.waitFor();
        result.nanoTimeTook = Units.Time.sinceNano(startNano);
        return result;
    }

    /**
     * Executes the command line.
     * @return True if result is successful, else false.
     */
    public boolean attempt() {
        return Sugar.either(() -> call().isSuccessful(), e -> false).get();
    }

    private Runnable getOutputReader(boolean isError) {
        return () -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(isError ? process.getErrorStream() :
                    process.getInputStream()))) {
                br.lines().forEach(line -> result.addOutput(new CommandLineOutputLine(line, isError)));
            } catch (IOException e) {
                result.addOutput(new CommandLineOutputLine(e.toString(), true));
            }
        };
    }

    /**
     * Returns the last computed result.
     */
    public CommandLineResult getLastResult() {
        return result;
    }

    /**
     * Destroy the process if started.
     */
    public void stop() {
        if (process != null)
            process.destroy();
    }
}
