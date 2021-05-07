package ezw.util;

import ezw.concurrent.Concurrent;
import ezw.concurrent.Interruptible;
import ezw.util.calc.Units;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * An OS command line process executor. Designed to be used directly or by extending.
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

        void setExitStatus(int exitStatus) {
            this.exitStatus = exitStatus;
        }

        /**
         * Returns the total process execution time in nanoseconds.
         */
        public long getNanoTimeTook() {
            return nanoTimeTook;
        }

        void setNanoTimeTook(long nanoTimeTook) {
            this.nanoTimeTook = nanoTimeTook;
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

    protected String[] command;
    private final boolean collectOutput;
    private PrintStream out;
    private PrintStream err;
    protected Process process;
    protected CommandLineResult result;
    protected long startNano;

    /**
     * Constructs a command line.
     * @param collectOutput If true, the standard output lines of the process are collected, else ignored.
     * @param command The command.
     */
    public CommandLine(boolean collectOutput, Object... command) {
        if (command.length == 0)
            throw new IllegalArgumentException("No commands received.");
        this.command = Arrays.stream(command).filter(Objects::nonNull).map(Object::toString).toArray(String[]::new);
        this.collectOutput = collectOutput;
        if (collectOutput)
            setOutputStreams(System.out, System.err);
    }

    /**
     * Sets the output streams printing the output lines collected. If null, the lines will not be printed. The default
     * streams are the standard output and error streams.
     */
    public void setOutputStreams(PrintStream out, PrintStream err) {
        this.out = out;
        this.err = err;
    }

    @Override
    public CommandLineResult call() throws IOException, InterruptedException, ExecutionException {
        Interruptible.validateInterrupted();
        result = new CommandLineResult(collectOutput);
        var builder = new ProcessBuilder(command);
        builder.redirectErrorStream(collectOutput);
        startNano = System.nanoTime();
        process = builder.start();
        if (collectOutput) {
            ExecutorService outputReadingPool = Executors.newFixedThreadPool(2);
            try {
                Concurrent.getAll(outputReadingPool.submit(getOutputReader(false)),
                        outputReadingPool.submit(getOutputReader(true)));
            } finally {
                outputReadingPool.shutdown();
            }
        }
        result.setExitStatus(process.waitFor());
        result.setNanoTimeTook(Units.Time.sinceNano(startNano));
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
